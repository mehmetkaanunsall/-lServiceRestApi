/**
 * This class ...
 *
 *
 * @author SALİM VELA ABDULHADİ
 *
 * @date   01.02.2018 11:26:07
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.Dictionary;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.List;

public class Authorize extends WotLogging {

    private int id;
    private Branch branch;
    private String name;
    private List<Integer> listOfModules;
    private List<Integer> listOfFolders;
    private List<Integer> listOfPages;
    private List<Integer> listOfTabs;
    private List<Integer> listOfButtons;
    private boolean isAdmin;

    public Authorize() {

        this.branch = new Branch();
        this.listOfModules = new ArrayList<>();
        this.listOfFolders = new ArrayList<>();
        this.listOfPages = new ArrayList<>();
        this.listOfTabs = new ArrayList<>();
        this.listOfButtons = new ArrayList<>();
    }

    public Authorize(int id) {
        this.id = id;
    }

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

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public List<Integer> getListOfModules() {
        return listOfModules;
    }

    public void setListOfModules(List<Integer> listOfModules) {
        this.listOfModules = listOfModules;
    }

    public List<Integer> getListOfFolders() {
        return listOfFolders;
    }

    public void setListOfFolders(List<Integer> listOfFolders) {
        this.listOfFolders = listOfFolders;
    }

    public List<Integer> getListOfPages() {
        return listOfPages;
    }

    public void setListOfPages(List<Integer> listOfPages) {
        this.listOfPages = listOfPages;
    }

    public List<Integer> getListOfTabs() {
        return listOfTabs;
    }

    public void setListOfTabs(List<Integer> listOfTabs) {
        this.listOfTabs = listOfTabs;
    }

    public List<Integer> getListOfButtons() {
        return listOfButtons;
    }

    public void setListOfButtons(List<Integer> listOfButtons) {
        this.listOfButtons = listOfButtons;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}
