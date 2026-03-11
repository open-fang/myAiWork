package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Lookup Controller
 * Uses JAX-RS annotations (@Path) for URL mapping
 * 提供下拉选项数据
 */
@Component
@Path("/api/lookup")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LookupController {

    /**
     * 获取通用下拉选项
     */
    @GET
    @Path("/{code}")
    public Response getLookupValues(@PathParam("code") String code) {
        List<LookupValue> values = switch (code) {
            case "authTargetLevel", "authPublishLevel" -> List.of(
                    new LookupValue("ORGANIZATION", "机关"),
                    new LookupValue("REGIONAL_DEPT", "地区部"),
                    new LookupValue("REPRESENTATIVE_OFFICE", "代表处"),
                    new LookupValue("OFFICE", "办事处")
            );
            case "applicableRegion" -> List.of(
                    new LookupValue("EAST", "华东"),
                    new LookupValue("NORTH", "华北"),
                    new LookupValue("SOUTH", "华南"),
                    new LookupValue("WEST", "西部"),
                    new LookupValue("CENTRAL", "华中")
            );
            default -> List.of();
        };
        return Response.ok(ApiResponse.success(values)).build();
    }

    /**
     * 获取组织树
     */
    @GET
    @Path("/org/tree")
    public Response getOrgTree() {
        List<OrgTreeNode> tree = List.of(
                new OrgTreeNode("ORG001", "总部", 0, List.of(
                        new OrgTreeNode("ORG002", "华东区", 1, List.of(
                                new OrgTreeNode("ORG003", "上海办事处", 2, List.of()),
                                new OrgTreeNode("ORG004", "杭州办事处", 2, List.of())
                        )),
                        new OrgTreeNode("ORG005", "华北区", 1, List.of(
                                new OrgTreeNode("ORG006", "北京办事处", 2, List.of()),
                                new OrgTreeNode("ORG007", "天津办事处", 2, List.of())
                        ))
                ))
        );
        return Response.ok(ApiResponse.success(tree)).build();
    }

    /**
     * 获取产业树
     */
    @GET
    @Path("/industry/tree")
    public Response getIndustryTree() {
        List<OrgTreeNode> tree = List.of(
                new OrgTreeNode("IND001", "ICT", 0, List.of(
                        new OrgTreeNode("IND001_1", "运营商", 1, List.of()),
                        new OrgTreeNode("IND001_2", "企业", 1, List.of())
                )),
                new OrgTreeNode("IND002", "消费者", 0, List.of(
                        new OrgTreeNode("IND002_1", "手机", 1, List.of()),
                        new OrgTreeNode("IND002_2", "PC", 1, List.of())
                )),
                new OrgTreeNode("IND003", "云计算", 0, List.of())
        );
        return Response.ok(ApiResponse.success(tree)).build();
    }

    /**
     * 获取业务场景选项
     */
    @GET
    @Path("/business-scenarios")
    public Response getBusinessScenarios() {
        List<LookupValue> values = List.of(
                new LookupValue("BS001", "设备销售"),
                new LookupValue("BS002", "软件销售"),
                new LookupValue("BS003", "服务销售"),
                new LookupValue("BS004", "物料采购")
        );
        return Response.ok(ApiResponse.success(values)).build();
    }

    /**
     * 获取决策层级选项
     */
    @GET
    @Path("/decision-levels")
    public Response getDecisionLevels() {
        List<LookupValue> values = List.of(
                new LookupValue("ORGANIZATION", "机关"),
                new LookupValue("REGIONAL_DEPT", "地区部"),
                new LookupValue("REPRESENTATIVE_OFFICE", "代表处"),
                new LookupValue("OFFICE", "办事处")
        );
        return Response.ok(ApiResponse.success(values)).build();
    }

    /**
     * 获取业务对象选项（用于规则参数配置）
     */
    @GET
    @Path("/business-objects")
    public Response getBusinessObjects() {
        List<LookupValue> values = List.of(
                new LookupValue("CONTRACT", "合同"),
                new LookupValue("ORDER", "订单"),
                new LookupValue("PROJECT", "项目"),
                new LookupValue("CUSTOMER", "客户"),
                new LookupValue("PRODUCT", "产品")
        );
        return Response.ok(ApiResponse.success(values)).build();
    }

    /**
     * 获取规则字段选项（用于条件配置）
     */
    @GET
    @Path("/rule-fields")
    public Response getRuleFields() {
        List<LookupValue> values = List.of(
                new LookupValue("amount", "金额"),
                new LookupValue("quantity", "数量"),
                new LookupValue("customer_level", "客户等级"),
                new LookupValue("product_type", "产品类型")
        );
        return Response.ok(ApiResponse.success(values)).build();
    }

    /**
     * Lookup值记录
     */
    public record LookupValue(String code, String name) {}

    /**
     * 树节点记录
     */
    public record OrgTreeNode(String code, String name, int level, List<OrgTreeNode> children) {}
}