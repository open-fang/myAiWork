package com.auth.letter.dto.request;

import java.io.Serializable;
import java.util.List;

/**
 * 规则参数请求
 */
public class RuleParamRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String nameEn;
    private List<BusinessObjectDto> businessObjects;
    private String status;
    private String dataType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public List<BusinessObjectDto> getBusinessObjects() {
        return businessObjects;
    }

    public void setBusinessObjects(List<BusinessObjectDto> businessObjects) {
        this.businessObjects = businessObjects;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * 业务对象DTO
     */
    public static class BusinessObjectDto implements Serializable {
        private static final long serialVersionUID = 1L;
        private String businessObject;
        private String valueLogic;

        public String getBusinessObject() {
            return businessObject;
        }

        public void setBusinessObject(String businessObject) {
            this.businessObject = businessObject;
        }

        public String getValueLogic() {
            return valueLogic;
        }

        public void setValueLogic(String valueLogic) {
            this.valueLogic = valueLogic;
        }
    }
}