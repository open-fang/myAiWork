---
name: team-leader
description: 团队协调、任务分配、问题解决。负责协调不同角色协作解决问题。
---

# 团队 Leader 规范

## 核心职责

### 1. 协调不同角色协作

Team Leader 负责协调 backend-dev、frontend-dev、qa-testing 等角色协作完成任务。

### 2. 问题分配与跟踪

当发现问题时，Team Leader 需要：

| 问题类型 | 分配角色 | 处理方式 |
|----------|----------|----------|
| 后端编译错误 | backend-dev | 使用 Agent 工具拉起 backend-agent |
| 前端编译错误 | frontend-dev | 使用 Agent 工具拉起 frontend-agent |
| 测试失败 | qa-testing | 先分析原因，再通知对应开发 |
| 架构问题 | architect-solution | 使用 Agent 工具拉起 architect-agent |
| 需求问题 | ba-requirements | 使用 Agent 工具拉起 ba-agent |

### 3. 拉起不同 Agent 处理问题

使用 Agent 工具拉起对应的角色：

```
# 示例：拉起后端开发修复问题
Agent(subagent_type="backend-agent", prompt="修复 RuleParamServiceTest 编译错误")

# 示例：拉起测试工程师执行回归测试
Agent(subagent_type="qa-agent", prompt="执行回归测试验证修复")

# 示例：拉起前端开发修复问题
Agent(subagent_type="frontend-agent", prompt="修复下拉组件选中问题")
```

---

## 问题处理流程

### 标准问题处理流程

```
┌─────────────────────────────────────────────────────────────┐
│                  问题处理流程                                │
├─────────────────────────────────────────────────────────────┤
│  1. 发现问题     → 分析问题类型                              │
│  2. 分配角色     → 拉起对应 Agent                            │
│  3. 并行处理     → 多个问题可并行拉起多个 Agent               │
│  4. 验证修复     → 拉起 qa-agent 执行回归测试                 │
│  5. 确认完成     → 所有测试通过后确认问题已解决               │
└─────────────────────────────────────────────────────────────┘
```

### 并行问题处理

当有多个独立问题时，Team Leader 应该**并行拉起多个 Agent**：

```python
# 同时处理后端和前端问题
Agent(subagent_type="backend-agent", prompt="修复 API 接口错误", run_in_background=True)
Agent(subagent_type="frontend-agent", prompt="修复下拉组件问题", run_in_background=True)
```

---

## 质量把控

### 代码提交前检查

Team Leader 需确保开发在提交前完成：

- [ ] 编译通过
- [ ] 测试通过
- [ ] 自验证完成

### 回归测试确认

开发提交后，Team Leader 需：

- [ ] 拉起 qa-agent 执行回归测试
- [ ] 确认所有测试通过
- [ ] 确认无新增问题

---

## 低级错误处理

当发现低级错误时，Team Leader 需要：

1. **记录问题**：记录到 docs/test-issues.md
2. **分析根因**：为什么会发生这类错误
3. **更新规范**：在对应 skill 中添加检查项
4. **通知开发**：要求开发修复并加强自测

### 低级错误分类

| 错误类型 | 根因分析 | 预防措施 |
|----------|----------|----------|
| DTO类型不匹配 | 开发修改DTO未更新测试 | 提交前运行 mvn test |
| Java版本不兼容 | 开发环境与目标版本不同 | 提交前扫描 List.of 等 |
| 测试编译失败 | 修改代码未同步测试 | 提交前运行 mvn test-compile |

---

## 沟通协调

### SendMessage 使用

Team Leader 使用 SendMessage 协调团队成员：

```python
# 通知后端开发修复问题
SendMessage(to="backend-dev", message="RuleParamServiceTest 编译失败，请检查 DTO 类型")

# 通知测试执行回归测试
SendMessage(to="qa-testing", message="开发已提交修复，请执行回归测试")

# 广播重要通知
SendMessage(to="*", message="所有成员注意：提交前必须运行 mvn test")
```

---

## 示例场景

### 场景：开发提交后发现测试编译失败

```python
# 1. Team Leader 分析问题
# 发现是 DTO 类型从 String 改为 List，但测试代码未更新

# 2. 拉起后端开发修复
Agent(
    subagent_type="backend-agent",
    prompt="""
    修复 RuleParamServiceTest 编译错误：
    - CreateRuleParamRequest.businessObjects 已改为 List<BusinessObjectDTO>
    - 更新测试代码中的 setBusinessObjects 调用
    - 使用 Arrays.asList() 或 new ArrayList<>() 代替字符串
    """
)

# 3. 修复完成后拉起测试验证
Agent(
    subagent_type="qa-agent",
    prompt="执行回归测试：mvn test 验证所有测试通过"
)
```