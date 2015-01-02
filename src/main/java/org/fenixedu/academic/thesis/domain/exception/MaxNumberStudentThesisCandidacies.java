package org.fenixedu.academic.thesis.domain.exception;

import org.fenixedu.academic.domain.student.Student;

public class MaxNumberStudentThesisCandidacies extends Exception {

    private final Student student;

    public MaxNumberStudentThesisCandidacies(Student student) {
	super();
	this.student = student;
    }

    public Student getStudent() {
	return student;
    }

}
