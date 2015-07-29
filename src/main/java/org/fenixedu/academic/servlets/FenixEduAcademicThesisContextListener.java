package org.fenixedu.academic.servlets;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.DegreeHelper;
import org.fenixedu.academic.domain.accessControl.PersistentCoordinatorGroup;
import org.fenixedu.bennu.core.signals.DomainObjectEvent;
import org.fenixedu.bennu.core.signals.Signal;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FenixEduAcademicThesisContextListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent servletContextEvent) {
        Signal.register(Degree.CREATED_SIGNAL, (DomainObjectEvent<Degree> event) -> {
            Degree degree = event.getInstance();
            PersistentCoordinatorGroup pcg = PersistentCoordinatorGroup.getInstance(degree.getDegreeType(), degree);
            DegreeHelper.setCanManageThesis(degree, pcg.toGroup());
        });
    }

    @Override public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
