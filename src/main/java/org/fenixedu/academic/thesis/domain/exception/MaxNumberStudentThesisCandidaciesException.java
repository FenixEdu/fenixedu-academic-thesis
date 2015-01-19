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
package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.domain.student.Student;

public class MaxNumberStudentThesisCandidaciesException extends Exception {

    private static final long serialVersionUID = 3422026637712388229L;
    private final Student student;

    public MaxNumberStudentThesisCandidaciesException(Student student) {
        super();
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

}
