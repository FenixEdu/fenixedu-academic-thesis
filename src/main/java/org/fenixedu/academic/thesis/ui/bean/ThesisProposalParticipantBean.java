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
package org.fenixedu.academic.thesis.ui.bean;

import org.fenixedu.academic.thesis.domain.ExternalUser;
import org.fenixedu.bennu.core.domain.User;

public class ThesisProposalParticipantBean {

    private User user;
    private String participantTypeExternalId;
    private int percentage;
    private String name;
    private String email;

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getUserExternalId() {
        return user != null ? user.getExternalId() : null;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public ThesisProposalParticipantBean(User user, String participantType, int percentage) {
        setUser(user);
        setPercentage(percentage);
        this.participantTypeExternalId = participantType;
    }

    public ThesisProposalParticipantBean(String name, String email, String participantType, int percentage) {
        setName(name);
        setEmail(email);
        setPercentage(percentage);
        this.participantTypeExternalId = participantType;
    }

    public ThesisProposalParticipantBean() {
    }

    public ThesisProposalParticipantBean(User user, ExternalUser externalUser, String participantType, int participationPercentage) {
        setUser(user);
        setPercentage(participationPercentage);
        this.participantTypeExternalId = participantType;

        if (externalUser != null) {
            setName(externalUser.getName());
            setEmail(externalUser.getEmail());
        }

    }

    public boolean isExternal() {
        return getName() != null || getEmail() != null;
    }

}
