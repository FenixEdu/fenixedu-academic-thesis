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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.thesis.domain.StudentThesisCandidacy;
import org.fenixedu.academic.thesis.domain.ThesisProposal;
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

    private List<Object> getHeaders(List<ThesisProposal> thesisProposals, int maxParticipants) {
        final List<Object> headers = new ArrayList<Object>();
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.number"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.title"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.state"));

        for (int i = 0; i < maxParticipants; i++) {
            headers.add(BundleUtil.getString(BUNDLE, "export.thesis.advisors.name") + (i + 1));
            headers.add(BundleUtil.getString(BUNDLE, "export.thesis.advisors.username") + (i + 1));
            headers.add(BundleUtil.getString(BUNDLE, "export.thesis.advisors.percentage") + (i + 1));
        }

        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.objectives"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.rqeuirements"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.observations"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.tfc.location"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.candidacy.accepted.name"));
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.candidacy.accepted.number"));
        return headers;
    }

    private List<Object> getGroupHeaders() {
        final List<Object> headers = new ArrayList<Object>();
        headers.add(BundleUtil.getString(BUNDLE, "export.thesis.student"));
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

        int maxParticipants = thesisProposals.stream().mapToInt(p -> p.getThesisProposalParticipantSet().size()).max().orElse(0);

        final List<Object> headers = getHeaders(thesisProposals, maxParticipants);

        final Spreadsheet spreadsheet =
                new Spreadsheet("proposals_" + executionDegree.getAcademicInterval().getStart().getYear() + "_"
                        + executionDegree.getAcademicInterval().getEnd().getYear(), headers);
        List<Set<StudentThesisCandidacy>> studentCandidacies = new ArrayList<Set<StudentThesisCandidacy>>();
        fillSpreadSheet(thesisProposals, spreadsheet, studentCandidacies, maxParticipants);

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

    private void fillProposalInfo(ThesisProposal proposal, final Spreadsheet spreadsheet, int maxParticipants) {
        final Row row = spreadsheet.addRow();
        row.setCell(proposal.getIdentifier());
        row.setCell(proposal.getTitle());
        row.setCell(proposal.getHidden() ? BundleUtil.getString(BUNDLE, "label.proposal.status.hidden") : BundleUtil.getString(
                BUNDLE, "label.proposal.status.visible"));

        proposal.getSortedParticipants().forEach(
                participant -> {
                    row.setCell(participant.getName());
                    row.setCell(participant.getUser() != null ? participant.getUser().getUsername() : BundleUtil.getString(
                            BUNDLE, "export.thesis.advisors.username.external"));
                    row.setCell(participant.getParticipationPercentage());
                });
        for (int i = proposal.getThesisProposalParticipantSet().size(); i < maxParticipants; i++) {
            row.setCell("");
            row.setCell("");
            row.setCell("");
        }

        row.setCell(proposal.getGoals());
        row.setCell(proposal.getRequirements());
        row.setCell(proposal.getObservations());
        row.setCell(proposal.getLocalization());

        Optional<StudentThesisCandidacy> acceptedCandidacy =
                proposal.getStudentThesisCandidacySet().stream().filter(candidacy -> candidacy.getAcceptedByAdvisor())
                        .findFirst();

        row.setCell(acceptedCandidacy.isPresent() ? acceptedCandidacy.get().getRegistration().getStudent().getName() : "");
        row.setCell(acceptedCandidacy.isPresent() ? "" + acceptedCandidacy.get().getRegistration().getStudent().getNumber() : "");
    }

    private void fillSpreadSheet(List<ThesisProposal> thesisProposals, final Spreadsheet spreadsheet,
            List<Set<StudentThesisCandidacy>> studentCandidacies, int maxParticipants) {
        thesisProposals.forEach(p -> {
            fillProposalInfo(p, spreadsheet, maxParticipants);
            studentCandidacies.add(p.getStudentThesisCandidacySet());
        });
    }
}
