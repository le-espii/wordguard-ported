/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.sk89q.worldguard.protection.association;

import com.google.common.base.Preconditions;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.association.ConstantAssociation;
import com.sk89q.worldguard.protection.association.RegionAssociable;

public final class Associables {
    private static final RegionAssociable OWNER_ASSOCIABLE = new ConstantAssociation(Association.OWNER);
    private static final RegionAssociable MEMBER_ASSOCIABLE = new ConstantAssociation(Association.MEMBER);
    private static final RegionAssociable NON_MEMBER_ASSOCIABLE = new ConstantAssociation(Association.NON_MEMBER);

    private Associables() {
    }

    public static RegionAssociable constant(Association association) {
        Preconditions.checkNotNull((Object)((Object)association));
        switch (association) {
            case OWNER: {
                return OWNER_ASSOCIABLE;
            }
            case MEMBER: {
                return MEMBER_ASSOCIABLE;
            }
            case NON_MEMBER: {
                return NON_MEMBER_ASSOCIABLE;
            }
        }
        return new ConstantAssociation(association);
    }
}

