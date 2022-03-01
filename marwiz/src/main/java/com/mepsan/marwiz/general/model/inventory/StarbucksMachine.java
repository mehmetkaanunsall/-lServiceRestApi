/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.04.2021 12:47:26
 */
package com.mepsan.marwiz.general.model.inventory;

public class StarbucksMachine {

    private int id;
    private String name;
    private String code;
    private String machineBarcode;
    private String pubKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMachineBarcode() {
        return machineBarcode;
    }

    public void setMachineBarcode(String machineBarcode) {
        this.machineBarcode = machineBarcode;
    }

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
