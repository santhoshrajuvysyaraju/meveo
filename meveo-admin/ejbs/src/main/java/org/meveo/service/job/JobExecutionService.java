/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.base.PersistenceService;

@Stateless
public class JobExecutionService extends PersistenceService<JobExecutionResultImpl> {

    @Inject
    private JobInstanceService jobInstanceService;

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void executeJob(String jobName, JobInstance jobInstance, User currentUser, JobCategoryEnum jobCategory) {
        try {
            HashMap<String, String> jobs = JobInstanceService.jobEntries.get(jobCategory);
            InitialContext ic = new InitialContext();
            Job job = (Job) ic.lookup(jobs.get(jobName));
            job.execute(jobInstance, currentUser);
        } catch (Exception e) {
            log.error("failed to execute timer job", e);
        }
    }

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persistResult(Job job, JobExecutionResult result, JobInstance jobInstance, User currentUser, JobCategoryEnum jobCategory) {
		try {
			log.info("JobExecutionService persistResult...");
			JobExecutionResultImpl entity = JobExecutionResultImpl.createFromInterface(jobInstance, result);
			boolean persistResult = false;

			if ((entity.getNbItemsCorrectlyProcessed() + entity.getNbItemsProcessedWithError() + entity.getNbItemsProcessedWithWarning()) > 0) {
				log.info(job.getClass().getName() +entity.toString());
				persistResult = true;
			} else {
				log.info(job.getClass().getName() + ": nothing to do");
				persistResult = "true".equals(ParamBean.getInstance().getProperty("meveo.job.persistResult", "true"));
			}
			if (persistResult) {
				if (entity.isTransient()) {
					create(entity, currentUser);
				} else {
					// search for job execution result
					JobExecutionResultImpl updateEntity = findById(result.getId());
					if(updateEntity != null){
						JobExecutionResultImpl.updateFromInterface(result, updateEntity);
					}
				}
				result.setId(entity.getId());
			}
			log.info("PersistResult entity.isDone()=" + entity.isDone());
			if (!entity.isDone()) {
				executeJob(job.getClass().getSimpleName(), jobInstance, currentUser, jobCategory);
			} else if (jobInstance.getFollowingJob() != null) {
				try {
					JobInstance jobFlow  = jobInstanceService.attach(jobInstance.getFollowingJob());
					executeJob(jobFlow.getJobTemplate(), jobFlow, currentUser, jobFlow.getJobCategoryEnum());
				} catch (Exception e) {
					log.warn("PersistResult cannot excute the following jobs.");
				}
			}
		} catch (Exception e) {// FIXME:BusinessException e) {
			log.error("error on persistResult", e);
		}
		log.info("JobExecutionService persistResult End");
	}

    private QueryBuilder getFindQuery(String jobName, PaginationConfiguration configuration,Provider provider) {
        String sql = "select distinct t from JobExecutionResultImpl t";
        QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();

        if (!StringUtils.isEmpty(jobName)) {
            qb.addCriterion("t.jobInstance.code", "=", jobName, false);
        }
        qb.addCriterionEntity("t.provider", provider);
        qb.addPaginationConfiguration(configuration);

        return qb;
    }

    /**
     * Count job execution history records which end date is older then a given date
     * 
     * @param date Date to check
     * @param provider Provider
     * @return A number of job execution history records which is older then a given date
     */
    public long countJobExecutionHistoryToDelete(String jobName, Date date, Provider currentProvider) {
        long result = 0;
        if (date != null) {
            String sql = "select t from JobExecutionResultImpl t";
            QueryBuilder qb = new QueryBuilder(sql);// FIXME:.cacheable();
            if (!StringUtils.isEmpty(jobName)) {
                qb.addCriterion("t.jobInstance.code", "=", jobName, false);
            }
            qb.addCriterion("t.startDate", "<", date, false);
            qb.addCriterionEntity("t.provider", currentProvider);
            result = qb.count(getEntityManager());
        }

        return result;
    }

    /**
     * Remove job execution history older then a given date
     * 
     * @param jobName Job name to match
     * @param date Date to check
     * @param provider Provider
     * @return A number of records that were removed
     */
    @SuppressWarnings("unchecked")
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int deleteJobExecutionHistory(String jobName, Date date, Provider provider) {
        log.trace("Removing {} job execution history older then a {} date for provider {}", jobName, date, provider);

        List<JobInstance> jobInstances = null;
        if (jobName != null) {
            QueryBuilder qb = new QueryBuilder("select ji from JobInstance ji");
            qb.addCriterion("ji.code", "=", jobName, false);
            jobInstances = qb.getQuery(getEntityManager()).getResultList();
            if (jobInstances.isEmpty()){
                log.info("Removed 0 job execution history which start date is older then a {} date for provider {}", date, provider);
                return 0;
            }
        }

        String sql = "delete from JobExecutionResultImpl t";
        QueryBuilder qb = new QueryBuilder(sql);
        if (jobName != null) {
            qb.addSqlCriterion("t.jobInstance in :jis", "jis", jobInstances);
        }
        qb.addCriterionDateRangeToTruncatedToDay("t.startDate", date);
        qb.addCriterionEntity("t.provider", provider);
        int itemsDeleted = qb.getQuery(getEntityManager()).executeUpdate();

        log.info("Removed {} job execution history which start date is older then a {} date for provider {}", itemsDeleted, date, provider);
        return itemsDeleted;
    }

    @SuppressWarnings("unchecked")
    public List<JobExecutionResultImpl> find(String jobName, PaginationConfiguration configuration,Provider provider) {
        return getFindQuery(jobName, configuration,provider).find(getEntityManager());
    }

    public long count(String jobName, PaginationConfiguration configuration,Provider provider) {
        return getFindQuery(jobName, configuration,provider).count(getEntityManager());
    }

    public JobInstanceService getJobInstanceService() {
        return jobInstanceService;
    }
    
    @Override
    public List<JobExecutionResultImpl> findByCodeLike(String code, Provider provider){
    	throw new UnsupportedOperationException();
    }    
}