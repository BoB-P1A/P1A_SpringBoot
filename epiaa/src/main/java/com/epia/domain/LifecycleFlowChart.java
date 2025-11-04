package com.epia.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("lifecycle_flowcharts")
public class LifecycleFlowChart {
    @Id
    public String id;
    public String companyId;
    public String taskName;

    public List<Node> nodes;
    public List<Link> links;

    public static class Node {
        public String key;
        public String text;
        public int x;
        public int y;
    }
    public static class Link {
        public String from;
        public String to;
    }
}