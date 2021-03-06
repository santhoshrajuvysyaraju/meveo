package org.meveo.api.billing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.ChargeInstanceOverrideDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.ServiceInstanceDto;
import org.meveo.api.dto.billing.ServiceToActivateDto;
import org.meveo.api.dto.billing.ServiceToInstantiateDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SubscriptionsDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.TerminationReasonService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

@Stateless
public class SubscriptionApi extends BaseApi {

    @Inject
    private SubscriptionService subscriptionService;

    @Inject
    private OfferTemplateService offerTemplateService;

    @Inject
    private UserAccountService userAccountService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private ServiceInstanceService serviceInstanceService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;

    @Inject
    private TerminationReasonService terminationReasonService;

    @Inject
    private WalletTemplateService walletTemplateService;

    @SuppressWarnings("rawtypes")
    @Inject
    private ChargeInstanceService chargeInstanceService;

    public void create(SubscriptionDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
        }
        if (StringUtils.isBlank(postData.getOfferTemplate())) {
            missingParameters.add("offerTemplate");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getSubscriptionDate())) {
            missingParameters.add("subscriptionDate");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        if (subscriptionService.findByCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(Subscription.class, postData.getCode());
        }

        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount(), provider);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
        }

        Subscription subscription = new Subscription();
        subscription.setCode(postData.getCode());
        subscription.setDescription(postData.getDescription());
        subscription.setUserAccount(userAccount);
        subscription.setOffer(offerTemplate);

        subscription.setSubscriptionDate(postData.getSubscriptionDate());
        subscription.setTerminationDate(postData.getTerminationDate());
        subscription.setEndAgreementDate(postData.getEndAgreementDate());

        subscriptionService.create(subscription, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), subscription, true, currentUser);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }

    }

    public void update(SubscriptionDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getUserAccount())) {
            missingParameters.add("userAccount");
        }
        if (StringUtils.isBlank(postData.getOfferTemplate())) {
            missingParameters.add("offerTemplate");
        }
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getSubscriptionDate())) {
            missingParameters.add("subscriptionDate");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        Subscription subscription = subscriptionService.findByCode(postData.getCode(), provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getCode());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
            throw new MeveoApiException("Subscription is already RESILIATED.");
        }

        UserAccount userAccount = userAccountService.findByCode(postData.getUserAccount(), provider);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, postData.getUserAccount());
        }

        OfferTemplate offerTemplate = offerTemplateService.findByCode(postData.getOfferTemplate(), provider);
        if (offerTemplate == null) {
            throw new EntityDoesNotExistsException(OfferTemplate.class, postData.getOfferTemplate());
        }

        subscription.setUserAccount(userAccount);
        subscription.setOffer(offerTemplate);
        subscription.setDescription(postData.getDescription());
        subscription.setSubscriptionDate(postData.getSubscriptionDate());
        subscription.setTerminationDate(postData.getTerminationDate());
        subscription.setEndAgreementDate(postData.getEndAgreementDate());

        subscription = subscriptionService.update(subscription, currentUser);

        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), subscription, false, currentUser);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw new MeveoApiException("Failed to associate custom field instance to an entity");
        }

    }

    public void activateServices(ActivateServicesRequestDto postData, User currentUser, boolean ignoreAlreadyActivatedError) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (postData.getServicesToActivateDto().getService() == null || postData.getServicesToActivateDto().getService().size() == 0) {
            missingParameters.add("services");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }

        // check if exists
        List<ServiceToActivateDto> serviceToActivateDtos = new ArrayList<>();
        for (ServiceToActivateDto serviceToActivateDto : postData.getServicesToActivateDto().getService()) {
            if (StringUtils.isBlank(serviceToActivateDto.getSubscriptionDate())) {
                missingParameters.add("SubscriptionDate");
                handleMissingParameters();
            }
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToActivateDto.getCode(), provider);
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToActivateDto.getCode());
            }

            serviceToActivateDto.setServiceTemplate(serviceTemplate);
            serviceToActivateDtos.add(serviceToActivateDto);
        }

        // instantiate
        List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();
        for (ServiceToActivateDto serviceToActivateDto : serviceToActivateDtos) {
            ServiceTemplate serviceTemplate = serviceToActivateDto.getServiceTemplate();
            log.debug("instanciateService id={} checked, quantity={}", serviceTemplate.getId(), 1);

            List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription);
            boolean alreadyActive = false;
            boolean alreadySuspended = false;
            ServiceInstance serviceInstance = null;
            for (ServiceInstance subscriptionServiceInstance : subscriptionServiceInstances) {
                if (subscriptionServiceInstance.getStatus() != InstanceStatusEnum.CANCELED && subscriptionServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED
                        && subscriptionServiceInstance.getStatus() != InstanceStatusEnum.CLOSED) {
                    if (subscriptionServiceInstance.getStatus().equals(InstanceStatusEnum.INACTIVE)) {
                        if (serviceToActivateDto.getSubscriptionDate() != null) {
                            log.warn("need date for serviceInstance with code={}", subscriptionServiceInstance.getCode());
                            // subscriptionServiceInstance.setDescription(serviceTemplate.getDescription()); // Is there a need to reset it?
                            subscriptionServiceInstance.setSubscriptionDate(serviceToActivateDto.getSubscriptionDate());
                            subscriptionServiceInstance.setQuantity(serviceToActivateDto.getQuantity());
                            serviceInstance = subscriptionServiceInstance;
                            serviceInstances.add(serviceInstance);
                        }
                    } else if (subscriptionServiceInstance.getStatus().equals(InstanceStatusEnum.ACTIVE)) {
                        serviceInstance = subscriptionServiceInstance;
                        alreadyActive = true;

                    } else {
                        alreadySuspended = true;
                    }
                    break;
                }

            }

            if (alreadyActive && !ignoreAlreadyActivatedError) {
                throw new MeveoApiException("ServiceInstance with code=" + serviceToActivateDto.getCode() + " must not be ACTIVE.");
            }

            if (alreadySuspended) {
                throw new MeveoApiException("ServiceInstance with code=" + serviceToActivateDto.getCode() + " must not be SUSPENDED.");
            }

            // Instantiate if it was not instantiated earlier
            if (serviceInstance == null) {
                serviceInstance = new ServiceInstance();
                serviceInstance.setProvider(serviceTemplate.getProvider());
                serviceInstance.setCode(serviceTemplate.getCode());
                serviceInstance.setDescription(serviceTemplate.getDescription());
                serviceInstance.setServiceTemplate(serviceTemplate);
                serviceInstance.setSubscription(subscription);

                if (serviceToActivateDto.getSubscriptionDate() == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    serviceInstance.setSubscriptionDate(calendar.getTime());
                } else {
                    serviceInstance.setSubscriptionDate(serviceToActivateDto.getSubscriptionDate());
                }
                serviceInstance.setQuantity(serviceToActivateDto.getQuantity());
                try {
                    serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);

                    if (serviceToActivateDto.getSubscriptionDate() != null) {
                        serviceInstances.add(serviceInstance);
                    }
                } catch (BusinessException e) {
                    log.error("Failed to instantiate a service {} on subscription {}", serviceToActivateDto.getCode(), subscription.getCode(), e);
                    throw new BusinessApiException(e.getMessage());
                }

                // populate customFields
                try {
                    populateCustomFields(serviceToActivateDto.getCustomFields(), serviceInstance, true, currentUser);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Failed to associate custom field instance to an entity {}", serviceToActivateDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + serviceToActivateDto.getCode());
                }
            }
        }

        // override price
        for (ServiceToActivateDto serviceToActivateDto : serviceToActivateDtos) {
            if (serviceToActivateDto.getChargeInstanceOverrides() != null && serviceToActivateDto.getChargeInstanceOverrides().getChargeInstanceOverride() != null) {
                for (ChargeInstanceOverrideDto chargeInstanceOverrideDto : serviceToActivateDto.getChargeInstanceOverrides().getChargeInstanceOverride()) {
                    if (!StringUtils.isBlank(chargeInstanceOverrideDto.getChargeInstanceCode()) && chargeInstanceOverrideDto.getAmountWithoutTax() != null) {
                        ChargeInstance chargeInstance = chargeInstanceService.findByCodeAndService(chargeInstanceOverrideDto.getChargeInstanceCode(), subscription.getId());
                        if (chargeInstance == null) {
                            throw new EntityDoesNotExistsException(ChargeInstance.class, chargeInstanceOverrideDto.getChargeInstanceCode());
                        }
                        chargeInstance.setAmountWithoutTax(chargeInstanceOverrideDto.getAmountWithoutTax());
                        if (!currentUser.getProvider().isEntreprise()) {
                            chargeInstance.setAmountWithTax(chargeInstanceOverrideDto.getAmountWithTax());
                        }
                    } else {
                        log.warn("chargeInstance.code and amountWithoutTax must not be null.");
                    }
                }
            }
        }

        // activate services
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (serviceInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
                throw new MeveoApiException("Service " + serviceInstance.getCode() + " is Suspended");
            }

            if (serviceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
                throw new MeveoApiException("Service " + serviceInstance.getCode() + " is already Active");
            }

            OfferTemplate offer = serviceInstance.getSubscription().getOffer();
            if (offer != null && !offer.containsServiceTemplate(serviceInstance.getServiceTemplate())) {
                throw new MeveoApiException("Service " + serviceInstance.getCode() + " is not associated with Offer");
            }

            try {
                serviceInstanceService.serviceActivation(serviceInstance, null, null, currentUser);
            } catch (BusinessException e) {
                throw new MeveoApiException(e.getMessage());
            }
        }

    }

    public void instantiateServices(InstantiateServicesRequestDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getSubscription())) {
            missingParameters.add("subscription");
        }
        if (postData.getServicesToInstantiate().getService() == null || postData.getServicesToInstantiate().getService().size() == 0) {
            missingParameters.add("services");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }

        // check if exists
        List<ServiceToInstantiateDto> serviceToInstantiateDtos = new ArrayList<>();
        for (ServiceToInstantiateDto serviceToInstantiateDto : postData.getServicesToInstantiate().getService()) {
            ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(serviceToInstantiateDto.getCode(), provider);
            if (serviceTemplate == null) {
                throw new EntityDoesNotExistsException(ServiceTemplate.class, serviceToInstantiateDto.getCode());
            }

            serviceToInstantiateDto.setServiceTemplate(serviceTemplate);
            serviceToInstantiateDtos.add(serviceToInstantiateDto);
        }

        // instantiate
        for (ServiceToInstantiateDto serviceToInstantiateDto : serviceToInstantiateDtos) {
            ServiceTemplate serviceTemplate = serviceToInstantiateDto.getServiceTemplate();
            log.debug("instanciateService id={} checked, quantity={}", serviceTemplate.getId(), 1);

            ServiceInstance serviceInstance = null;
            List<ServiceInstance> subscriptionServiceInstances = serviceInstanceService.findByCodeSubscriptionAndStatus(serviceTemplate.getCode(), subscription);
            boolean alreadyinstanciated = false;
            for (ServiceInstance subscriptionServiceInstance : subscriptionServiceInstances) {
                if (subscriptionServiceInstance.getStatus() != InstanceStatusEnum.CANCELED && subscriptionServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED
                        && subscriptionServiceInstance.getStatus() != InstanceStatusEnum.CLOSED) {
                    alreadyinstanciated = true;
                    break;
                }

            }

            if (alreadyinstanciated) {
                throw new MeveoApiException("ServiceInstance with code=" + serviceToInstantiateDto.getCode() + " must instanciated.");
            }
            if (serviceInstance == null) {
                serviceInstance = new ServiceInstance();
                serviceInstance.setProvider(serviceTemplate.getProvider());
                serviceInstance.setCode(serviceTemplate.getCode());
                serviceInstance.setDescription(serviceTemplate.getDescription());
                serviceInstance.setServiceTemplate(serviceTemplate);
                serviceInstance.setSubscription(subscription);

                if (serviceToInstantiateDto.getSubscriptionDate() == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    serviceInstance.setSubscriptionDate(calendar.getTime());
                } else {
                    serviceInstance.setSubscriptionDate(serviceToInstantiateDto.getSubscriptionDate());
                }
                serviceInstance.setQuantity(serviceToInstantiateDto.getQuantity());
                try {
                    serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);

                } catch (BusinessException e) {
                    log.error("Failed to instantiate a service {} on subscription {}", serviceToInstantiateDto.getCode(), subscription.getCode(), e);
                    throw new BusinessApiException(e.getMessage());
                }

                // populate customFields
                try {
                    populateCustomFields(serviceToInstantiateDto.getCustomFields(), serviceInstance, true, currentUser);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    log.error("Failed to associate custom field instance to an entity {}", serviceToInstantiateDto.getCode(), e);
                    throw new MeveoApiException("Failed to associate custom field instance to an entity " + serviceToInstantiateDto.getCode());
                }
            }
        }
    }

    public void applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getOneShotCharge())) {
            missingParameters.add("oneShotCharge");
        }
        if (StringUtils.isBlank(StringUtils.isBlank(postData.getSubscription()))) {
            missingParameters.add("subscription");
        }
        if (postData.getOperationDate() == null) {
            missingParameters.add("operationDate");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        OneShotChargeTemplate oneShotChargeTemplate = oneShotChargeTemplateService.findByCode(postData.getOneShotCharge(), provider);
        if (oneShotChargeTemplate == null) {
            throw new EntityDoesNotExistsException(OneShotChargeTemplate.class, postData.getOneShotCharge());
        }

        Subscription subscription = subscriptionService.findByCode(postData.getSubscription(), provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscription());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED || subscription.getStatus() == SubscriptionStatusEnum.CANCELED) {
            throw new MeveoApiException("Subscription is already RESILIATED or CANCELLED.");
        }
        if (postData.getWallet() != null) {
            WalletTemplate walletTemplate = walletTemplateService.findByCode(postData.getWallet(), provider);
            if (walletTemplate == null) {
                throw new EntityDoesNotExistsException(WalletTemplate.class, postData.getWallet());
            }

            if ((!postData.getWallet().equals("PRINCIPAL")) && !subscription.getUserAccount().getPrepaidWallets().containsKey(postData.getWallet())) {
                if (postData.getCreateWallet() != null && postData.getCreateWallet()) {
                    subscription.getUserAccount().getWalletInstance(postData.getWallet());
                } else {
                    throw new MeveoApiException("Wallet " + postData.getWallet() + " is not attached to the user account, but were instructed not to create it");
                }
            }
        }

        try {
            oneShotChargeInstanceService.oneShotChargeApplication(subscription, (OneShotChargeTemplate) oneShotChargeTemplate, postData.getWallet(), postData.getOperationDate(),
                postData.getAmountWithoutTax(), postData.getAmountWithTax(), postData.getQuantity(), postData.getCriteria1(), postData.getCriteria2(), postData.getCriteria3(),
                postData.getDescription(), currentUser, true);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }

    public void terminateSubscription(TerminateSubscriptionRequestDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getSubscriptionCode())) {
            missingParameters.add("subscriptionCode");
        }
        if (StringUtils.isBlank(postData.getTerminationReason())) {
            missingParameters.add("terminationReason");
        }
        if (postData.getTerminationDate() == null) {
            missingParameters.add("terminationDate");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        Subscription subscription = subscriptionService.findByCode(postData.getSubscriptionCode(), currentUser.getProvider());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode());
        }

        if (subscription.getStatus() == SubscriptionStatusEnum.RESILIATED) {
            throw new MeveoApiException("Subscription is already RESILIATED.");
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getTerminationReason(), provider);
        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
        }

        try {
            subscriptionService.terminateSubscription(subscription, postData.getTerminationDate(), subscriptionTerminationReason, currentUser);
        } catch (BusinessException e) {
            log.error("error while setting subscription termination", e);
            throw new MeveoApiException(e.getMessage());
        }
    }

    public void terminateServices(TerminateSubscriptionServicesRequestDto postData, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(postData.getSubscriptionCode())) {
            missingParameters.add("subscriptionCode");
        }
        if (postData.getServices() == null || postData.getServices().size() == 0) {
            missingParameters.add("services");
        }
        if (StringUtils.isBlank(postData.getTerminationReason())) {
            missingParameters.add("terminationReason");
        }
        if (postData.getTerminationDate() == null) {
            missingParameters.add("terminationDate");
        }

        handleMissingParameters();

        Provider provider = currentUser.getProvider();

        Subscription subscription = subscriptionService.findByCode(postData.getSubscriptionCode(), currentUser.getProvider());
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, postData.getSubscriptionCode());
        }

        SubscriptionTerminationReason serviceTerminationReason = terminationReasonService.findByCode(postData.getTerminationReason(), provider);
        if (serviceTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getTerminationReason());
        }

        for (String serviceInstanceCode : postData.getServices()) {
            ServiceInstance serviceInstance = serviceInstanceService.findActivatedByCodeAndSubscription(serviceInstanceCode, subscription);
            if (serviceInstance != null) {
                try {
                    serviceInstanceService.terminateService(serviceInstance, postData.getTerminationDate(), serviceTerminationReason, currentUser);
                } catch (BusinessException e) {
                    log.error("service termination={}", e.getMessage());
                    throw new MeveoApiException(e.getMessage());
                }
            } else {
                throw new MeveoApiException("ServiceInstance with code=" + serviceInstanceCode + " must be ACTIVE.");
            }
        }
    }

    public SubscriptionsDto listByUserAccount(String userAccountCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(userAccountCode)) {
            missingParameters.add("userAccountCode");
            handleMissingParameters();
        }

        UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
        if (userAccount == null) {
            throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
        }

        SubscriptionsDto result = new SubscriptionsDto();
        List<Subscription> subscriptions = subscriptionService.listByUserAccount(userAccount);
        if (subscriptions != null) {
            for (Subscription s : subscriptions) {
                result.getSubscription().add(subscriptionToDto(s));
            }
        }

        return result;

    }

    public SubscriptionDto findSubscription(String subscriptionCode, Provider provider) throws MeveoApiException {
        SubscriptionDto result = new SubscriptionDto();

        if (StringUtils.isBlank(subscriptionCode)) {
            missingParameters.add("subscriptionCode");
            handleMissingParameters();
        }
        Subscription subscription = subscriptionService.findByCode(subscriptionCode, provider);
        if (subscription == null) {
            throw new EntityDoesNotExistsException(Subscription.class, subscriptionCode);
        }

        result = subscriptionToDto(subscription);

        return result;
    }

    /**
     * Create or update Subscription based on subscription code
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public void createOrUpdate(SubscriptionDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (subscriptionService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    public SubscriptionDto subscriptionToDto(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setCode(subscription.getCode());
        dto.setDescription(subscription.getDescription());

        if (subscription.getUserAccount() != null) {
            dto.setUserAccount(subscription.getUserAccount().getCode());
        }

        if (subscription.getOffer() != null) {
            dto.setOfferTemplate(subscription.getOffer().getCode());
        }

        dto.setSubscriptionDate(subscription.getSubscriptionDate());
        dto.setTerminationDate(subscription.getTerminationDate());
        dto.setEndAgreementDate(subscription.getEndAgreementDate());

        if (subscription.getAccessPoints() != null) {
            for (Access ac : subscription.getAccessPoints()) {
                dto.getAccesses().getAccess().add(new AccessDto(ac, entityToDtoConverter.getCustomFieldsDTO(ac)));
            }
        }

        dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(subscription));

        if (subscription.getServiceInstances() != null) {
            for (ServiceInstance serviceInstance : subscription.getServiceInstances()) {
                dto.getServices().getServiceInstance().add(new ServiceInstanceDto(serviceInstance, entityToDtoConverter.getCustomFieldsDTO(serviceInstance)));
            }
        }
        return dto;
    }
}
