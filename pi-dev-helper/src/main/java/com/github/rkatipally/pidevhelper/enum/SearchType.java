package net.atpco.pi.pistrategyskipper.enums;

public enum SearchType {

    BY_FARE(1),
    SIMPLE_BY_FARE(2);


    private int typeId;

    SearchType(int typeId) {
        this.typeId = typeId;
    }

}
