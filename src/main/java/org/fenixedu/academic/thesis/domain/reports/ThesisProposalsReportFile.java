package org.fenixedu.academic.thesis.domain.reports;

import org.fenixedu.academic.domain.QueueJobResult;
import org.fenixedu.academic.domain.reports.GepReportFile;
import org.fenixedu.academic.thesis.domain.ThesisProposalsConfiguration;
import org.fenixedu.academic.thesis.ui.service.ExportThesisProposalsService;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.spreadsheet.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ThesisProposalsReportFile extends ThesisProposalsReportFile_Base {

    private static final Logger logger = LoggerFactory.getLogger(GepReportFile.class);

    public ThesisProposalsReportFile() {
        super();
    }

    private String getReportName(ThesisProposalsConfiguration tp) {
        return "proposals_" + tp.getPresentationName().replace("/", "_");
    }

    @Override
    public String getJobName() {
        return "Listagem de propostas de dissertações";
    }

    @Override
    protected String getPrefix() {
        return "propostas de dissertações";
    }

    @Override
    public void renderReport(Spreadsheet spreadsheet) {
        //not used
    }

    @Override
    public QueueJobResult execute() throws Exception {
        final Set<ThesisProposalsConfiguration> thesisProposalsConfigurations = new HashSet<>();
        Bennu.getInstance().getExecutionDegreesSet().stream().filter(ed -> ed.getExecutionYear() == getExecutionYear())
                .forEach(ed -> {
                            List<ThesisProposalsConfiguration> tpc = ed.getThesisProposalsConfigurationSet().stream().distinct()
                                    .sorted(ThesisProposalsConfiguration.COMPARATOR_BY_PROPOSAL_PERIOD_START_DESC)
                                    .collect(Collectors.toList());
                            if (!tpc.isEmpty()) {
                                thesisProposalsConfigurations.add(tpc.get(0));
                            }
                        }
                );

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ZipOutputStream zip = new ZipOutputStream(bout);
        thesisProposalsConfigurations.stream().forEach(tp -> {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExportThesisProposalsService proposalsService = new ExportThesisProposalsService();
            try {
                proposalsService.exportThesisProposalsToExcel(tp, bos);
                zip.putNextEntry(new ZipEntry(getReportName(tp) + ".xls"));
                zip.write(bos.toByteArray());
                zip.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        zip.close();

        final QueueJobResult queueJobResult = new QueueJobResult();
        queueJobResult.setContentType("application/zip");
        queueJobResult.setContent(bout.toByteArray());

        logger.info("Job " + getFilename() + " completed");

        return queueJobResult;
    }
}
