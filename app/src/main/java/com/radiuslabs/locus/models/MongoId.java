package com.radiuslabs.locus.models;

import java.io.Serializable;

public class MongoId implements Serializable {

    private String $oid;

    public String get$oid() {
        return $oid;
    }

    public void set$oid(String $oid) {
        this.$oid = $oid;
    }

}
