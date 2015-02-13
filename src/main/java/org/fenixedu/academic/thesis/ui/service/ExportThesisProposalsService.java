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
package org.fenixedu.academic.thesis.ui.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipant;
import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.utl.ist.fenix.tools.util.excel.Spreadsheet;
import pt.utl.ist.fenix.tools.util.excel.Spreadsheet.Row;

@Service
public class ExportThesisProposalsService {

    @Autowired
    ThesisProposalsService service;
    static String BUNDLE = "resources.FenixEduThesisProposalsResources";

    private List<Object> getHeaders() {
        final List<Object> headers = new ArrayList<Object>();
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.number"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.state"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.title"));

        service.getThesisProposalParticipantTypes().stream().forEach(type -> {
            headers.add(type.getName().getContent() + " - " + BundleUtil.getString(BUNDLE, "export.thesis.user.name"));
            headers.add(type.getName().getContent() + " - " + BundleUtil.getString(BUNDLE, "export.thesis.user.username"));
        });
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.advisor.percent"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.coadvisor.percent"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.external.tutor.name"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.external.tutor.mail"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.external.tutor.phone"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.external.company.name"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.external.company.address"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.scope"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.objectives"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.description"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.rqeuirements"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.expected.results"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.url"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.specialization.area"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student.min"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student.max"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.adequate"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.observations"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.tfc.location"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student.number"));
        //headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student.name"));
        return headers;
    }

    private List<Object> getGroupHeaders() {
        final List<Object> headers = new ArrayList<Object>();
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student") + 1);
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.prefAttr"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.pref") + 1);
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.pref") + 2);
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.pref") + 3);
        return headers;
    }

    public void exportThesisProposalsToExcel(ThesisProposalsConfiguration configuration, OutputStream outputStream)
            throws IOException {
        ExecutionDegree executionDegree = configuration.getExecutionDegree();
        List<ThesisProposal> thesisProposals = configuration.getThesisProposalSet().stream().collect(Collectors.toList());
        List<String> metaKeys = new ArrayList<String>();
        final List<Object> headers = getHeaders();
        final Spreadsheet spreadsheet =
                new Spreadsheet("proposals_" + executionDegree.getAcademicInterval().getStart().getYear() + "_"
                        + executionDegree.getAcademicInterval().getEnd().getYear(), headers);
        List<Set<StudentThesisCandidacy>> studentCandidacies = new ArrayList<Set<StudentThesisCandidacy>>();
        fillSpreadSheet(thesisProposals, spreadsheet, studentCandidacies);
        Spreadsheet newTab =
                spreadsheet.addSpreadsheet("groups_" + executionDegree.getAcademicInterval().getStart().getYear() + "_"
                        + executionDegree.getAcademicInterval().getEnd().getYear(), getGroupHeaders());
        fillGroups(studentCandidacies, newTab);
        fillCoursesStudents(studentCandidacies, newTab);
        spreadsheet.exportToXLSSheet(outputStream);
    }

    private void fillGroups(List<Set<StudentThesisCandidacy>> studentCandidacies, Spreadsheet spreadsheet) {
        for (Set<StudentThesisCandidacy> stc : studentCandidacies) {
            if (stc.isEmpty()) {
                continue;
            }
            final Row row = spreadsheet.addRow();
            row.setCell(stc.iterator().next().getRegistration().getNumber());
            row.setCell(stc.stream().sorted(StudentThesisCandidacy.COMPARATOR_BY_PREFERENCE_NUMBER)
                    .filter(cand -> cand.getAcceptedByAdvisor() == true).findFirst()
                    .map(s -> s.getThesisProposal().getIdentifier()).orElse(""));
            stc.stream().sorted(StudentThesisCandidacy.COMPARATOR_BY_PREFERENCE_NUMBER).forEach(cand -> {
                row.setCell(cand.getThesisProposal().getIdentifier());
            });
        }
    }

    private void fillCourseStudentsAux(Degree d, Set<String> students, Spreadsheet spreadsheet) {
        Row row;
        for (String s : students) {
            row = spreadsheet.addRow();
            row.setCell(s);
        }
    }

    private void fillCoursesStudents(List<Set<StudentThesisCandidacy>> studentCandidacies, Spreadsheet spreadsheet) {
        Map<Degree, Set<String>> translationMap = new HashMap<Degree, Set<String>>();
        List<Registration> regs =
                studentCandidacies.stream().flatMap(m -> m.stream()).map(stc -> stc.getRegistration())
                        .collect(Collectors.toList());
        for (Registration r : regs) {
            Set<String> names;
            if (translationMap.containsKey(r.getDegree())) {
                names = translationMap.get(r.getDegree());
            } else {
                names = new HashSet<String>();
                translationMap.put(r.getDegree(), names);
            }
            names.add(r.getNumber().toString());
        }
        List<Object> headers = new ArrayList<Object>();
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student"));
        Spreadsheet newSheet = spreadsheet;
        for (Degree d : translationMap.keySet()) {
            newSheet = newSheet.addSpreadsheet(d.getSigla(), headers);
            fillCourseStudentsAux(d, translationMap.get(d), newSheet);
        }
    }

    private void fillProposalInfo(ThesisProposal proposal, final Spreadsheet spreadsheet) {
        final Row row = spreadsheet.addRow();
        row.setCell(proposal.getIdentifier());
        //row.setCell(proposal.getState());
        row.setCell(proposal.getTitle());
        String name;
        String userName;
        List<ThesisProposalParticipantType> tppt = service.getThesisProposalParticipantTypes();
        tppt.stream().forEach(
                type -> {
                    List<ThesisProposalParticipant> thesisParticipant =
                            proposal.getThesisProposalParticipantSet().stream()
                                    .sorted(ThesisProposalParticipant.COMPARATOR_BY_WEIGHT)
                                    .filter(tpp -> tpp.getThesisProposalParticipantType() == type).collect(Collectors.toList());
                    row.setCell(thesisParticipant.stream().map(a -> a.getUser().getPerson().getName())
                            .collect(Collectors.joining(", ")));
                    row.setCell(thesisParticipant.stream().map(a -> a.getUser().getPerson().getUsername())
                            .collect(Collectors.joining(", ")));
                });
        row.setCell(proposal.getGoals());
        row.setCell(proposal.getRequirements());
        row.setCell(proposal.getObservations());
        row.setCell(proposal.getLocalization());
    }

    private void fillSpreadSheet(List<ThesisProposal> thesisProposals, final Spreadsheet spreadsheet,
            List<Set<StudentThesisCandidacy>> studentCandidacies) {
        for (ThesisProposal proposal : thesisProposals) {
            if (proposal.getHidden() == false) {
                fillProposalInfo(proposal, spreadsheet);
            }
            studentCandidacies.add(proposal.getStudentThesisCandidacySet());
        }
    }
}
