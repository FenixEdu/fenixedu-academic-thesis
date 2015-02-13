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
package org.fenixedu.academic.thesis.ui.exception;

import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;

public class UnequivalentThesisConfigurationsException extends ThesisProposalException {

    private static final long serialVersionUID = -4270028206922579262L;
    private ThesisProposalsConfiguration configuration0;
    private ThesisProposalsConfiguration configuration1;

    public ThesisProposalsConfiguration getConfiguration0() {
        return configuration0;
    }

    public void setConfiguration0(ThesisProposalsConfiguration configuration0) {
        this.configuration0 = configuration0;
    }

    public ThesisProposalsConfiguration getConfiguration1() {
        return configuration1;
    }

    public void setConfiguration1(ThesisProposalsConfiguration configuration1) {
        this.configuration1 = configuration1;
    }

    public UnequivalentThesisConfigurationsException(ThesisProposalsConfiguration configuration0,
            ThesisProposalsConfiguration configuration1) {
        this.configuration0 = configuration0;
        this.configuration1 = configuration1;
    }

}