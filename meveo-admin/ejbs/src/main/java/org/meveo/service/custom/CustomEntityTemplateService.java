/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.custom;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Stateless
public class CustomEntityTemplateService extends PersistenceService<CustomEntityTemplate> {

    @Inject
    CustomFieldTemplateService customFieldTemplateService;

    @Override
    public void remove(Long id) {

        CustomEntityTemplate cet = findById(id);

        List<CustomFieldTemplate> fields = customFieldTemplateService.findByAppliesTo(cet.getCFTPrefix(), cet.getProvider());

        for (CustomFieldTemplate cft : fields) {
            customFieldTemplateService.remove(cft);
        }
        super.remove(id);
    }
}