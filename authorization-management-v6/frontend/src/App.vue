<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', Arial, sans-serif;
  font-size: 14px;
  color: #333;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
}

/* 通用样式 */
.page-container {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 40px);
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e8e8e8;
}

/* 表单样式 */
.form-row {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 16px;
  gap: 16px;
}

.form-item {
  display: flex;
  align-items: center;
  flex: 0 0 calc(33.33% - 11px);
  min-width: 250px;
}

.form-item label {
  width: 100px;
  text-align: right;
  margin-right: 8px;
  color: #666;
  flex-shrink: 0;
}

.form-item label.required::before {
  content: '*';
  color: #ff4d4f;
  margin-right: 4px;
}

.form-item input,
.form-item textarea {
  flex: 1;
  height: 32px;
  padding: 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #1890ff;
  outline: none;
}

.form-item textarea {
  height: 80px;
  resize: vertical;
}

/* 按钮样式 */
.btn {
  display: inline-block;
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 4px;
  border: 1px solid #d9d9d9;
  background: #fff;
  cursor: pointer;
  margin-right: 8px;
}

.btn-primary {
  background: #1890ff;
  border-color: #1890ff;
  color: #fff;
}

.btn-primary:hover {
  background: #40a9ff;
}

.btn-danger {
  color: #ff4d4f;
  border-color: #ff4d4f;
}

.btn-danger:hover {
  background: #fff1f0;
}

.btn-link {
  color: #1890ff;
  border: none;
  background: transparent;
  padding: 0 4px;
}

.btn-link:hover {
  color: #40a9ff;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 表格样式 */
.table-wrapper {
  margin-top: 16px;
}

.table-toolbar {
  margin-bottom: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #e8e8e8;
}

th, td {
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #e8e8e8;
}

th {
  background: #fafafa;
  font-weight: 500;
}

tr:hover {
  background: #f5f5f5;
}

.table-actions {
  display: flex;
  gap: 8px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  gap: 8px;
}

.pagination span {
  color: #666;
}

.pagination button {
  padding: 4px 12px;
  border: 1px solid #d9d9d9;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination select {
  height: 28px;
  padding: 0 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

/* 操作图标 */
.icon-edit {
  color: #1890ff;
  cursor: pointer;
  font-size: 16px;
}

.icon-delete {
  color: #ff4d4f;
  cursor: pointer;
  font-size: 16px;
}

/* 状态标签 */
.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-draft {
  background: #f0f0f0;
  color: #666;
}

.status-published {
  background: #e6f7ff;
  color: #1890ff;
}

.status-invalid {
  background: #fff1f0;
  color: #ff4d4f;
}

/* 自定义下拉选择器 */
.custom-select {
  position: relative;
  flex: 1;
}

.select-trigger {
  display: flex;
  align-items: center;
  min-height: 32px;
  padding: 4px 30px 4px 11px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  position: relative;
  flex-wrap: wrap;
  gap: 4px;
}

.select-trigger:hover {
  border-color: #40a9ff;
}

.select-trigger .arrow {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border: 5px solid transparent;
  border-top-color: #999;
}

.select-trigger .placeholder {
  color: #bfbfbf;
}

.select-tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  background: #f0f0f0;
  border-radius: 4px;
  font-size: 12px;
  margin: 2px;
}

.select-tag .close {
  margin-left: 4px;
  cursor: pointer;
  font-size: 14px;
}

.select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 4px;
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
}

.select-option {
  padding: 8px 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.select-option:hover {
  background: #f5f5f5;
}

.select-option input {
  margin-right: 8px;
}

/* 树形选择器 */
.tree-select-dropdown {
  padding: 8px;
}

.tree-node {
  padding: 4px 0;
}

.tree-node-content {
  display: flex;
  align-items: center;
  padding: 4px 8px;
  cursor: pointer;
}

.tree-node-content:hover {
  background: #f5f5f5;
}

.tree-node-content input {
  margin-right: 8px;
}

.tree-toggle {
  width: 20px;
  cursor: pointer;
  color: #999;
}

.tree-children {
  padding-left: 20px;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  background: #fff;
  border-radius: 8px;
  min-width: 600px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 16px;
}

.modal-close {
  cursor: pointer;
  font-size: 20px;
  color: #999;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  text-align: right;
}

/* 规则配置样式 */
.rule-config {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 16px;
  margin-top: 16px;
}

.rule-condition-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  padding: 8px;
  background: #fafafa;
  border-radius: 4px;
}

.rule-condition-row select,
.rule-condition-row input {
  height: 32px;
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.logic-toggle {
  display: inline-block;
  padding: 4px 12px;
  background: #e6f7ff;
  color: #1890ff;
  border-radius: 4px;
  cursor: pointer;
  margin: 8px 0;
}

.condition-group {
  border: 1px dashed #1890ff;
  border-radius: 4px;
  padding: 12px;
  margin: 8px 0;
  background: #f0f5ff;
}

/* 底部按钮栏 */
.footer-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid #e8e8e8;
  text-align: center;
  z-index: 100;
}

/* 折叠面板 */
.collapse-panel {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-top: 16px;
}

.collapse-header {
  padding: 12px 16px;
  background: #fafafa;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collapse-header:hover {
  background: #f5f5f5;
}

.collapse-content {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
}

.collapse-icon {
  transition: transform 0.3s;
}

.collapse-icon.collapsed {
  transform: rotate(-90deg);
}

/* 链接样式 */
.link {
  color: #1890ff;
  text-decoration: none;
  cursor: pointer;
}

.link:hover {
  color: #40a9ff;
  text-decoration: underline;
}
</style>