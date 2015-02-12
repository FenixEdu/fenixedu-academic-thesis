/**
 * Copyright © 2014 Instituto Superior Técnico
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
import org.fenixedu.bennu.core.domain.User;

public class ThesisProposalParticipant extends ThesisProposalParticipant_Base {

    public final static Comparator<ThesisProposalParticipant> COMPARATOR_BY_WEIGHT = new Comparator<ThesisProposalParticipant>() {
        @Override
        public int compare(ThesisProposalParticipant arg0, ThesisProposalParticipant arg1) {
            return arg1.getThesisProposalParticipantType().getWeight() - arg0.getThesisProposalParticipantType().getWeight();
        }
    };

    public ThesisProposalParticipant(User user, ThesisProposalParticipantType participantType, int percentage) {
        super();
        setThesisProposalParticipantType(participantType);
        setUser(user);
        if (percentage >= 0 && percentage <= 100) {
            setParticipationPercentage(percentage);
        } else {
            throw new DomainException("error.domain.percentage.out.of.bounds");
        }
    }

    public void delete() {

        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        this.getThesisProposalParticipantType().getThesisProposalParticipantSet().remove(this);
        this.setThesisProposalParticipantType(null);

        this.setUser(null);
        this.setThesisProposal(null);

        deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);
    }
}
