package org.fenixedu.academic.thesis.ui.bean;

import org.fenixedu.academic.thesis.domain.ThesisProposalParticipantType;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class ParticipantTypeBean {

    private LocalizedString name;
    private int weight = 0;
    private String externalId;

    public LocalizedString getName() {
	return name;
    }

    public void setName(LocalizedString name) {
	this.name = name;
    }

    public int getWeight() {
	return weight;
    }

    public void setWeight(int weight) {
	this.weight = weight;
    }

    public String getExternalId() {
	return externalId;
    }

    public void setExternalId(String externalId) {
	this.externalId = externalId;
    }

    public ParticipantTypeBean(LocalizedString name, int weight) {
	this.name = name;
	this.weight = weight;
    }

    public ParticipantTypeBean(LocalizedString name, int weight, String externalId) {
	this.name = name;
	this.weight = weight;
	this.externalId = externalId;
    }

    public ParticipantTypeBean() {
    }

    public static class Builder {
	private final LocalizedString name;
	private final int weight;

	public Builder(ParticipantTypeBean thesisProposalParticipantTypeBean) {
	    this.name = thesisProposalParticipantTypeBean.getName();
	    this.weight = thesisProposalParticipantTypeBean.getWeight();
	}

	@Atomic(mode = TxMode.WRITE)
	public ThesisProposalParticipantType build() {

	    return new ThesisProposalParticipantType(name, weight);
	}
    }

}
