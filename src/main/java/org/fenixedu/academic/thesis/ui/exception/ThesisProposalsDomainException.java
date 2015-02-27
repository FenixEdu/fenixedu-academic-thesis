/**
 * Copyright © 2014 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Spaces.
 *
 * FenixEdu Spaces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Spaces is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.thesis.ui.exception;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response.Status;

public class ThesisProposalsDomainException extends org.fenixedu.bennu.core.domain.exceptions.DomainException {

    private static final String DEFAULT_BUNDLE = "resources.FenixEduThesisProposalsResources";

    protected ThesisProposalsDomainException(final String key, final String... args) {
        super(DEFAULT_BUNDLE, key, args);
    }

    protected ThesisProposalsDomainException(Status status, String key, String... args) {
        super(status, DEFAULT_BUNDLE, key, args);
    }

    protected ThesisProposalsDomainException(final String key, final Throwable cause, final String... args) {
        super(cause, DEFAULT_BUNDLE, key, args);
    }

    public static void throwWhenDeleteBlocked(Collection<String> blockers) {
        if (!blockers.isEmpty()) {
            throw new ThesisProposalsDomainException("key.return.argument", blockers.stream().collect(Collectors.joining(", ")));
        }
    }
}