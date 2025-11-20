package com.epia.seq;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("counters") public class Counter { @Id public String sequenceName; public int value; }