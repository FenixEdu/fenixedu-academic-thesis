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
package org.fenixedu.academic.thesis.domain.accessControl;

import java.util.stream.Stream;

import org.fenixedu.academic.domain.accessControl.FenixGroupStrategy;
import org.fenixedu.academic.thesis.domain.ThesisProposalsSystem;
import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.joda.time.DateTime;

@GroupOperator(ThesisCreatorsGroup.THESIS_CREATORS)
public class ThesisCreatorsGroup extends FenixGroupStrategy {

    private static final long serialVersionUID = -6661148802972245890L;

    public static final String THESIS_CREATORS = "thesisCreators";

    @Override
    public Stream<User> getMembers() {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().getMembers();
    }

    @Override
    public Stream<User> getMembers(DateTime when) {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().getMembers(when);
    }

    @Override
    public boolean isMember(User user) {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().isMember(user);

    }

    @Override
    public boolean isMember(User user, DateTime when) {
        return ThesisProposalsSystem.getInstance().getCanCreateThesisGroup().isMember(user, when);

    }

}
