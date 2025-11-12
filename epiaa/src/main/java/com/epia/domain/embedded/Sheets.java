package com.epia.domain.embedded;

import java.util.ArrayList;
import java.util.List;

public class Sheets {
    public List<CollectData> collect;
    public List<RetainData> retain;
    public List<UseData> use;
    public List<ProvideData> provide;
    public List<DiscardData> discard;

    public Sheets() {
        this.collect = new ArrayList<>();
        this.retain = new ArrayList<>();
        this.use = new ArrayList<>();
        this.provide = new ArrayList<>();
        this.discard = new ArrayList<>();
    }
}