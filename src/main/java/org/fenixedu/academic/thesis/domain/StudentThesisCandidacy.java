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
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;

public class StudentThesisCandidacy extends StudentThesisCandidacy_Base {

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_PREFERENCE_NUMBER =
            new Comparator<StudentThesisCandidacy>() {

                @Override
                public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
                    return arg0.getPreferenceNumber() - arg1.getPreferenceNumber();
                }
            };

    public final static Comparator<StudentThesisCandidacy> COMPARATOR_BY_DATETIME = new Comparator<StudentThesisCandidacy>() {

        @Override
        public int compare(StudentThesisCandidacy arg0, StudentThesisCandidacy arg1) {
            return arg0.getTimestamp().compareTo(arg1.getTimestamp());
        }
    };

    public StudentThesisCandidacy(Registration registration, Integer preferenceNumber, ThesisProposal thesisProposal) {
        super();
        setThesisProposalsSystem(ThesisProposalsSystem.getInstance());
        setPreferenceNumber(preferenceNumber);
        setAcceptedByAdvisor(false);
        setThesisProposal(thesisProposal);
        setRegistration(registration);
        setTimestamp(new DateTime());
    }

    public void delete() {

        DomainException.throwWhenDeleteBlocked(getDeletionBlockers());

        this.setThesisProposal(null);
        this.setThesisProposalsSystem(null);
        this.setRegistration(null);

        deleteDomainObject();
    }

    @Override
    protected void checkForDeletionBlockers(Collection<String> blockers) {
        super.checkForDeletionBlockers(blockers);

        if (getAcceptedByAdvisor()
                || !getThesisProposal().getSingleThesisProposalsConfiguration().getCandidacyPeriod().contains(DateTime.now())) {
            blockers.add(BundleUtil.getString("resources.FenixEduThesisProposalsResources", "error.candidacies.cant.delete"));
        }
    }

    @Override
    public DateTime getTimestamp() {
        return super.getTimestamp();
    }
}
