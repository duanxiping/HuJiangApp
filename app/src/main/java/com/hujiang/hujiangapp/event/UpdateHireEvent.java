package com.hujiang.hujiangapp.event;

import com.hujiang.hujiangapp.model.Hire;

public class UpdateHireEvent {
    private Hire hire;

    public UpdateHireEvent(Hire hire) {
        this.hire = hire;
    }

    public Hire getHire() {
        return hire;
    }

    public void setHire(Hire hire) {
        this.hire = hire;
    }
}
