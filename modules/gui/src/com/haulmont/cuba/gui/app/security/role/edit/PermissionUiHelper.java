/*
 * Copyright (c) 2011 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */

package com.haulmont.cuba.gui.app.security.role.edit;

import com.google.common.base.Predicate;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.security.RestorablePermissionDatasource;
import com.haulmont.cuba.security.entity.Permission;
import com.haulmont.cuba.security.entity.PermissionType;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.ui.PermissionVariant;
import org.apache.commons.lang.ObjectUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * <p>$Id$</p>
 *
 * @author artamonov
 */
public class PermissionUiHelper {

    public static PermissionVariant getCheckBoxVariant(Object value, PermissionVariant activeVariant) {
        PermissionVariant permissionVariant;
        if (Boolean.TRUE.equals(value))
            permissionVariant = activeVariant;
        else
            permissionVariant = PermissionVariant.NOTSET;
        return permissionVariant;
    }

    public static int getPermissionValue(PermissionVariant permissionVariant) {
        int value = 0;
        if (permissionVariant != PermissionVariant.NOTSET) {
            // Create permission
            switch (permissionVariant) {
                case ALLOWED:
                    value = PermissionValue.ALLOW.getValue();
                    break;

                case DISALLOWED:
                    value = PermissionValue.DENY.getValue();
                    break;
            }
        }
        return value;
    }

    /**
     * Add or edit permission item in datasource
     * @param ds Datasource
     * @param roleDs Role darasource
     * @param permissionTarget Permission identifier
     * @param type Permission type
     * @param value Permission value
     */
    public static void createPermissionItem(CollectionDatasource<Permission, UUID> ds, Datasource<Role> roleDs,
                                            final String permissionTarget, PermissionType type, Integer value) {
        final Collection<UUID> permissionIds = ds.getItemIds();

        Permission permission = null;
        for (UUID id : permissionIds) {
            Permission p = ds.getItem(id);
            if (ObjectUtils.equals(p.getTarget(), permissionTarget)) {
                permission = p;
                break;
            }
        }

        if (permission == null) {
            // workaround for idx_sec_permission_unique
            // restore entity instead of create
            if (ds instanceof RestorablePermissionDatasource) {
                RestorablePermissionDatasource datasource = (RestorablePermissionDatasource) ds;

                permission = datasource.findRemovedEntity(new Predicate<Permission>() {
                    @Override
                    public boolean apply(@Nullable Permission p) {
                        if (p != null)
                            return ObjectUtils.equals(p.getTarget(), permissionTarget);
                        return false;
                    }
                });
                if (permission != null)
                    datasource.restoreEntity(permission);
            }
        }

        if (permission == null) {
            final Permission newPermission = new Permission();
            newPermission.setRole(roleDs.getItem());
            newPermission.setTarget(permissionTarget);
            newPermission.setType(type);
            newPermission.setValue(value);

            ds.addItem(newPermission);
        } else {
            permission.setValue(value);
        }
    }
}