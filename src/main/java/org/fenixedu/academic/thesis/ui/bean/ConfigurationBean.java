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

import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.exception.OverlappingIntervalsException;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ConfigurationBean {

    private String proposalPeriodStart;
    private String proposalPeriodEnd;
    private String candidacyPeriodStart;
    private String candidacyPeriodEnd;
    private ExecutionDegree executionDegree;
    private String externalId;
    private int maxThesisCandidaciesByStudent;
    private int maxThesisProposalsByUser;
    private int minECTS1stCycle;
    private int minECTS2ndCycle;

    public String getProposalPeriodStart() {
        return proposalPeriodStart;
    }

    public void setProposalPeriodStart(String proposalPeriodStart) {
        this.proposalPeriodStart = proposalPeriodStart;
    }

    public String getProposalPeriodEnd() {
        return proposalPeriodEnd;
    }

    public void setProposalPeriodEnd(String proposalPeriodEnd) {
        this.proposalPeriodEnd = proposalPeriodEnd;
    }

    public String getCandidacyPeriodStart() {
        return candidacyPeriodStart;
    }

    public void setCandidacyPeriodStart(String candidacyPeriodStart) {
        this.candidacyPeriodStart = candidacyPeriodStart;
    }

    public String getCandidacyPeriodEnd() {
        return candidacyPeriodEnd;
    }

    public void setCandidacyPeriodEnd(String candidacyPeriodEnd) {
        this.candidacyPeriodEnd = candidacyPeriodEnd;
    }

    public ExecutionDegree getExecutionDegree() {
        return executionDegree;
    }

    public void setExecutionDegree(ExecutionDegree executionDegree) {
        this.executionDegree = executionDegree;
    }

    public int getMaxThesisCandidaciesByStudent() {
        return maxThesisCandidaciesByStudent;
    }

    public void setMaxThesisCandidaciesByStudent(int maxThesisCandidaciesByStudent) {
        this.maxThesisCandidaciesByStudent = maxThesisCandidaciesByStudent;
    }

    public int getMaxThesisProposalsByUser() {
        return maxThesisProposalsByUser;
    }

    public void setMaxThesisProposalsByUser(int maxThesisProposalsByUser) {
        this.maxThesisProposalsByUser = maxThesisProposalsByUser;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public int getMinECTS1stCycle() {
        return minECTS1stCycle;
    }

    public void setMinECTS1stCycle(int minECTS1stCycle) {
        this.minECTS1stCycle = minECTS1stCycle;
    }

    public int getMinECTS2ndCycle() {
        return minECTS2ndCycle;
    }

    public void setMinECTS2ndCycle(int minECTS2ndCycle) {
        this.minECTS2ndCycle = minECTS2ndCycle;
    }

    public ConfigurationBean(String proposalPeriodStart, String proposalPeriodEnd, String candidacyPeriodStart,
            String candidacyPeriodEnd, ExecutionDegree executionDegree, int maxThesisCandidaciesByStudent,
            int maxThesisProposalsByUser, int minECTS1stCycle, int minECTS2ndCycle) {
        this.proposalPeriodStart = proposalPeriodStart;
        this.proposalPeriodEnd = proposalPeriodEnd;
        this.candidacyPeriodStart = candidacyPeriodStart;
        this.candidacyPeriodEnd = candidacyPeriodEnd;
        this.executionDegree = executionDegree;
        this.maxThesisCandidaciesByStudent = maxThesisCandidaciesByStudent;
        this.maxThesisProposalsByUser = maxThesisProposalsByUser;
        this.minECTS1stCycle = minECTS1stCycle;
        this.minECTS2ndCycle = minECTS2ndCycle;
    }

    public ConfigurationBean() {
    }

    public ConfigurationBean(DateTime proposalPeriodStart, DateTime proposalPeriodEnd, DateTime candidacyPeriodStart,
            DateTime candidacyPeriodEnd, ExecutionDegree executionDegree, String externalId, int maxThesisCandidaciesByStudent,
            int maxThesisProposalsByUser, int minECTS1stCycle, int minECTS2ndCycle) {

        this.proposalPeriodStart = proposalPeriodStart.toString();
        this.proposalPeriodEnd = proposalPeriodEnd.toString();
        this.candidacyPeriodStart = candidacyPeriodStart.toString();
        this.candidacyPeriodEnd = candidacyPeriodEnd.toString();

        this.executionDegree = executionDegree;
        this.externalId = externalId;
        this.maxThesisCandidaciesByStudent = maxThesisCandidaciesByStudent;
        this.maxThesisProposalsByUser = maxThesisProposalsByUser;

        this.minECTS1stCycle = minECTS1stCycle;
        this.minECTS2ndCycle = minECTS2ndCycle;
    }

    public static class Builder {
        private final String proposalPeriodStart;
        private final String proposalPeriodEnd;
        private final String candidacyPeriodStart;
        private final String candidacyPeriodEnd;
        private final ExecutionDegree executionDegree;
        private final int maxThesisCandidaciesByStudent;
        private final int maxThesisProposalsByUser;
        private final int minECTS1stCycle;
        private final int minECTS2ndCycle;

        public Builder(ConfigurationBean thesisProposalsConfigurationBean) {

            this.proposalPeriodStart = thesisProposalsConfigurationBean.getProposalPeriodStart();
            this.proposalPeriodEnd = thesisProposalsConfigurationBean.getProposalPeriodEnd();
            this.candidacyPeriodStart = thesisProposalsConfigurationBean.getCandidacyPeriodStart();
            this.candidacyPeriodEnd = thesisProposalsConfigurationBean.getCandidacyPeriodEnd();
            this.executionDegree = thesisProposalsConfigurationBean.getExecutionDegree();
            this.maxThesisCandidaciesByStudent = thesisProposalsConfigurationBean.getMaxThesisCandidaciesByStudent();
            this.maxThesisProposalsByUser = thesisProposalsConfigurationBean.getMaxThesisProposalsByUser();
            this.minECTS1stCycle = thesisProposalsConfigurationBean.getMinECTS1stCycle();
            this.minECTS2ndCycle = thesisProposalsConfigurationBean.getMinECTS2ndCycle();
        }

        @Atomic(mode = TxMode.WRITE)
        public ThesisProposalsConfiguration build() throws OverlappingIntervalsException {

            DateTimeFormatter formatter = ISODateTimeFormat.dateTime();

            DateTime proposalPeriodStartDT = formatter.parseDateTime(proposalPeriodStart);
            DateTime proposalPeriodEndDT = formatter.parseDateTime(proposalPeriodEnd);
            DateTime candidacyPeriodStartDT = formatter.parseDateTime(candidacyPeriodStart);
            DateTime candidacyPeriodEndDT = formatter.parseDateTime(candidacyPeriodEnd);

            Interval proposalPeriod = new Interval(proposalPeriodStartDT, proposalPeriodEndDT);
            Interval candidacyPeriod = new Interval(candidacyPeriodStartDT, candidacyPeriodEndDT);

            for (ThesisProposalsConfiguration config : executionDegree.getThesisProposalsConfigurationSet()) {

                if (config.getProposalPeriod().overlaps(proposalPeriod) || config.getCandidacyPeriod().overlaps(candidacyPeriod)
                        || config.getProposalPeriod().overlaps(candidacyPeriod)
                        || config.getCandidacyPeriod().overlaps(proposalPeriod)) {
                    throw new OverlappingIntervalsException();
                }
            }

            return new ThesisProposalsConfiguration(proposalPeriod, candidacyPeriod, executionDegree,
                    maxThesisCandidaciesByStudent, maxThesisProposalsByUser, minECTS1stCycle, minECTS2ndCycle);
        }
    }

}
