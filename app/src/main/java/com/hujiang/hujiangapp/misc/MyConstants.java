package com.hujiang.hujiangapp.misc;

public class MyConstants {
    public static final int pageSize = 20;
    public static final int RequestCodeWorkType = 1001;

    public static final String DictCategoryBank = "BANK_CATEGORY";
    public static final String DictEmpCategory = "EMP_CATEGORY";
    public static final String DictJobTypeName = "JOB_TYPENAME";

    public static final String ExtraScanType = "ExtraScanType";
    public static final String ExtraFrontCamera = "ExtraFrontCamera";
    public static final String ExtraHireJson = "ExtraHireJson";

    public static final String KeySavedUsername = "KeySavedUsername";

    public enum ScanType {
        IdCardFront, IdCardBack, BankCard;

        public String getTypeString() {
            switch (this) {
                case IdCardFront:
                    return "face";
                case IdCardBack:
                    return "back";
                case BankCard:
                    return "bankcard";
            }
            return "";
        }
    }
}
