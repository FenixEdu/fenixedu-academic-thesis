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
package org.fenixedu.academic.thesis.ui.bean;

import org.fenixedu.bennu.core.domain.User;

public class ThesisProposalParticipantBean {

    private User user;
    private String participantTypeExternalId;

    public String getUserExternalId() {
        return user.getExternalId();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getParticipantTypeExternalId() {
        return participantTypeExternalId;
    }

    public void setParticipantTypeExternalId(String participantType) {
        this.participantTypeExternalId = participantType;
    }

    public ThesisProposalParticipantBean(User user, String participantType) {
        setUser(user);
        this.participantTypeExternalId = participantType;
    }

    public ThesisProposalParticipantBean() {
    }

    public User getUser() {
        return user;
    }

}
