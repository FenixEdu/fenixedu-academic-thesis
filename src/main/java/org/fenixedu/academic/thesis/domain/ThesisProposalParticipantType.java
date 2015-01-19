/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of FenixEdu Academic Thesis.
 *
 * FenixEdu Academic Thesis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Academic Thesis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Academic Thesis.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.thesis.domain;

import java.util.Collection;
import java.util.Comparator;

import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.i18n.LocalizedString;

public class ThesisProposalParticipantType extends ThesisProposalParticipantType_Base {

    public final static Comparator<ThesisProposalParticipantType> COMPARATOR_BY_WEIGHT =
            new Comparator<ThesisProposalParticipantType>() {

                @Override
                public int compare(ThesisProposalParticipantType arg0, ThesisProposalParticipantType arg1) {
                    return arg1.getWeight() - arg0.getWeight();
                }
            };

    public ThesisProposalParticipantType() {
        super();
    }

    public ThesisProposalParticipantType(LocalizedString name, int weight) {
        super();
        setName(name);
        setWeight(weight);
        setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
        ThesisProposalsSystem.getInstance().getThesisProposalParticipantTypeSet().add(this);
    }

    public void delete() {
        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        this.getThesisProposalParticipantSet().clear();
        this.setThesisProposalsSystem(null);

        deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
        if (!getThesisProposalParticipantSet().isEmpty()) {
            blockers.add(BundleUtil.getString("resources.ThesisProposalsResources", "error.participants.type.cant.delete"));
        }
    }
}
