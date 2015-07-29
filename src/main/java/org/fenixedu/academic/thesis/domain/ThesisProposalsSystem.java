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

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeHelper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.groups.Group;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ThesisProposalsSystem extends ThesisProposalsSystem_Base {

    private ThesisProposalsSystem() {
        super();
        setBennu(Bennu.getInstance());
        setCanManageGroup(Group.managers());
        setCanCreateThesisGroup(Group.dynamic("thesisCreators"));
        setProposalsCounter(0);
    }

    public static ThesisProposalsSystem getInstance() {
        if (Bennu.getInstance().getThesisProposalsSystem() == null) {
            return initialize();
        }
        return Bennu.getInstance().getThesisProposalsSystem();
    }

    @Atomic(mode = TxMode.WRITE)
    private static ThesisProposalsSystem initialize() {
        if (Bennu.getInstance().getThesisProposalsSystem() == null) {
            return new ThesisProposalsSystem();
        }
        return Bennu.getInstance().getThesisProposalsSystem();
    }

    public Group getCanCreateThesisGroup() {
        return getThesisCreatorsGroup().toGroup();
    }

    @Atomic
    public void setCanManageGroup(Group group) {
        setThesisSystemManager(group.toPersistentGroup());
    }

    public Group getCanManageThesisGroup() {
        return getThesisSystemManager().toGroup();
    }

    @Atomic
    public void setCanCreateThesisGroup(Group group) {
        setThesisCreatorsGroup(group.toPersistentGroup());
    }

    public static boolean canManage(Degree degree, User user) {
        return DegreeHelper.getCanManageThesis(degree).isMember(user)
                || ThesisProposalsSystem.getInstance().getCanManageThesisGroup().isMember(user);
    }

    public String generateProposalIdentifier() {
        int counter = getProposalsCounter();
        counter++;

        this.setProposalsCounter(counter);

        return "" + counter;
    }

}
