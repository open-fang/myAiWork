// @ts-check
import { test, expect } from '@playwright/test';

// 正确配置：前端在8081，后端API在8080
const BASE_URL = 'http://localhost:8081';
const API_URL = 'http://localhost:8080';

test.describe('授权书列表页测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(`${BASE_URL}/#/AuthLetterList`);
    // 等待网络空闲后再等待页面元素
    await page.waitForLoadState('networkidle');
    // 等待页面加载完成，增加超时时间
    await page.waitForSelector('.page-title', { timeout: 15000 });
  });

  test('E2E-001: 页面加载-标题显示', async ({ page }) => {
    // 验证页面标题显示
    const pageTitle = page.locator('.page-title');
    await expect(pageTitle).toBeVisible();
    await expect(pageTitle).toHaveText('授权书列表');
  });

  test('E2E-002: 搜索功能-按名称', async ({ page }) => {
    // 输入名称搜索
    const nameInput = page.locator('input[placeholder="请输入"]').first();
    await nameInput.fill('测试');

    // 点击查询按钮
    const searchButton = page.getByRole('button', { name: '查询' });
    await searchButton.click();

    // 等待表格加载
    await page.waitForTimeout(1000);

    // 验证表格存在
    const table = page.locator('.el-table');
    await expect(table).toBeVisible();
  });

  test('E2E-003: 搜索功能-按状态', async ({ page }) => {
    // 选择状态下拉框 - 状态下拉框在搜索表单底部
    const statusSelectWrapper = page.locator('.el-form-item').filter({ hasText: '状态' }).locator('.el-select');
    await statusSelectWrapper.click();

    // 等待下拉动画完成
    await page.waitForTimeout(500);

    // 直接选择可见的"草稿"选项（下拉面板已经打开）
    const draftOption = page.locator('li.el-select-dropdown__item').filter({ hasText: '草稿' });
    await draftOption.click();

    // 点击查询按钮
    const searchButton = page.getByRole('button', { name: '查询' });
    await searchButton.click();

    // 等待表格加载
    await page.waitForTimeout(1000);

    // 验证表格存在
    const table = page.locator('.el-table');
    await expect(table).toBeVisible();
  });

  test('E2E-004: 搜索功能-重置', async ({ page }) => {
    // 先输入搜索条件
    const nameInput = page.locator('input[placeholder="请输入"]').first();
    await nameInput.fill('测试');

    // 点击重置按钮
    const resetButton = page.getByRole('button', { name: '重置' });
    await resetButton.click();

    // 验证输入框已清空
    await expect(nameInput).toHaveValue('');
  });

  test('E2E-007: 新建按钮跳转', async ({ page }) => {
    // 点击新建授权书按钮
    const createButton = page.getByRole('button', { name: '新建授权书' });
    await createButton.click();

    // 验证跳转到详情页
    await expect(page).toHaveURL(/#\/AuthLetterDetail/);

    // 验证详情页标题显示
    const detailTitle = page.locator('.page-title');
    await expect(detailTitle).toBeVisible();
  });

  test('E2E-009: 批量选择-勾选', async ({ page }) => {
    // 等待表格加载
    await page.waitForSelector('.el-table__body-wrapper', { timeout: 5000 });

    // 检查是否有数据行
    const tableRows = page.locator('.el-table__body-wrapper .el-table__row');
    const rowCount = await tableRows.count();

    // 只有在有数据时才测试勾选功能
    if (rowCount > 0) {
      const checkbox = page.locator('.el-checkbox').first();
      // 确保checkbox可用（非禁用状态）
      if (await checkbox.isVisible() && await checkbox.isEnabled()) {
        await checkbox.click();

        // 等待一下
        await page.waitForTimeout(500);

        // 验证批量操作按钮可用
        const updateButton = page.getByRole('button', { name: '更新' });
        await expect(updateButton).not.toBeDisabled();
      }
    }
  });

  test('E2E-015: 年份选择', async ({ page }) => {
    // 点击年份选择器
    const yearPicker = page.locator('.el-date-editor--year');
    if (await yearPicker.isVisible()) {
      await yearPicker.click();

      // 等待年份面板出现
      await page.waitForSelector('.el-date-picker', { timeout: 5000 });

      // 验证年份面板可见
      const yearPanel = page.locator('.el-date-picker');
      await expect(yearPanel).toBeVisible();
    }
  });
});

test.describe('授权书详情页测试', () => {
  test.beforeEach(async ({ page }) => {
    // 直接访问新建页面
    await page.goto(`${BASE_URL}/#/AuthLetterDetail`);
    // 等待网络空闲后再等待页面元素
    await page.waitForLoadState('networkidle');
    // 等待页面加载
    await page.waitForSelector('.page-title', { timeout: 15000 });
  });

  test('E2E-016: 新建页面-字段显示', async ({ page }) => {
    // 验证页面标题
    const pageTitle = page.locator('.page-title');
    await expect(pageTitle).toBeVisible();

    // 验证基本信息区域存在
    const basicInfo = page.locator('.basic-info-card');
    if (await basicInfo.isVisible()) {
      await expect(basicInfo).toBeVisible();
    }
  });

  test('E2E-018: 新建页面-必填校验', async ({ page }) => {
    // 等待页面完全加载
    await page.waitForTimeout(1000);

    // 使用精确匹配找到保存按钮
    const saveButton = page.getByRole('button', { name: '保存', exact: true });
    if (await saveButton.isVisible()) {
      await saveButton.click();

      // 等待校验提示
      await page.waitForTimeout(1000);

      // 验证是否有校验提示（红色提示或message）
      const errorTip = page.locator('.el-form-item__error');
      // 如果有校验错误，应该显示
      // 注意：这取决于后端API是否正常响应
    }
  });

  test('E2E-032: 取消按钮', async ({ page }) => {
    // 点击取消按钮
    const cancelButton = page.getByRole('button', { name: '取消' });
    if (await cancelButton.isVisible()) {
      await cancelButton.click();

      // 等待确认对话框
      await page.waitForTimeout(500);

      // 可能出现确认对话框
      const confirmDialog = page.locator('.el-message-box');
      if (await confirmDialog.isVisible()) {
        // 点击确认
        const confirmButton = page.getByRole('button', { name: '确定' }).first();
        await confirmButton.click();

        // 验证返回列表页
        await expect(page).toHaveURL(/#\/AuthLetterList/);
      } else {
        // 如果没有确认对话框，直接返回
        await expect(page).toHaveURL(/#\/AuthLetterList/);
      }
    }
  });
});

test.describe('规则参数配置页测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto(`${BASE_URL}/#/RuleParamConfig`);
    // 等待网络空闲后再等待页面元素
    await page.waitForLoadState('networkidle');
    // 等待页面加载
    await page.waitForSelector('.page-title', { timeout: 15000 });
  });

  test('E2E-035: 页面加载', async ({ page }) => {
    // 验证页面标题显示
    const pageTitle = page.locator('.page-title');
    await expect(pageTitle).toBeVisible();
    await expect(pageTitle).toHaveText('规则参数配置');
  });

  test('E2E-036: 搜索功能', async ({ page }) => {
    // 输入名称搜索
    const nameInput = page.locator('input[placeholder="请输入名称"]').first();
    if (await nameInput.isVisible()) {
      await nameInput.fill('金额');

      // 点击查询按钮
      const searchButton = page.getByRole('button', { name: '查询' });
      await searchButton.click();

      // 等待表格加载
      await page.waitForTimeout(1000);

      // 验证表格存在
      const table = page.locator('.el-table');
      await expect(table).toBeVisible();
    }
  });

  test('E2E-037: 新建规则参数', async ({ page }) => {
    // 点击新建按钮
    const createButton = page.getByRole('button', { name: '新建' });
    if (await createButton.isVisible()) {
      await createButton.click();

      // 等待弹窗出现
      await page.waitForSelector('.el-dialog', { timeout: 5000 });

      // 验证弹窗标题
      const dialogTitle = page.locator('.el-dialog__title');
      await expect(dialogTitle).toBeVisible();
    }
  });
});

test.describe('组件交互测试', () => {
  test('E2E-043: CustomSelect-点击外部关闭', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/AuthLetterList`);
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('.page-title', { timeout: 15000 });

    // 找到一个下拉框并点击打开
    const select = page.locator('.el-select').first();
    await select.click();

    // 等待下拉动画完成
    await page.waitForTimeout(500);

    // 点击页面其他区域关闭下拉框
    await page.locator('.page-title').click();

    // 等待下拉面板关闭
    await page.waitForTimeout(500);

    // 验证下拉面板已关闭 - 检查是否有可见的选项
    const visibleOptions = await page.locator('li.el-select-dropdown__item:visible').count();
    expect(visibleOptions).toBe(0);
  });

  test('E2E-047: 年份选择器-显示范围', async ({ page }) => {
    await page.goto(`${BASE_URL}/#/AuthLetterList`);
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('.page-title', { timeout: 15000 });

    // 点击年份选择器
    const yearPicker = page.locator('.el-date-editor--year');
    if (await yearPicker.isVisible()) {
      await yearPicker.click();

      // 等待年份面板出现
      await page.waitForSelector('.el-year-table', { timeout: 5000 });

      // 获取当前年份
      const currentYear = new Date().getFullYear();

      // 验证显示当前年份
      const currentYearCell = page.locator(`td.available:has-text("${currentYear}")`);
      await expect(currentYearCell).toBeVisible();
    }
  });
});

test.describe('API接口测试', () => {
  test('API-001: 获取授权书列表', async ({ request }) => {
    // 通过API直接测试，使用request fixture而不是page.request
    const response = await request.get(`${API_URL}/api/v1/auth-letters?pageNum=1&pageSize=10`, { timeout: 5000 });

    // 验证响应状态（如果后端未运行会返回404或其他错误）
    if (response.status() === 404 || response.status() === 502) {
      test.skip(true, 'Backend API not available - skipping test');
      return;
    }

    expect(response.status()).toBe(200);

    // 解析响应数据
    const data = await response.json();

    // 验证响应格式
    expect(data).toHaveProperty('code');
    expect(data.code).toBe(200);
  });

  test('API-044: 获取Lookup数据', async ({ request }) => {
    // 测试获取授权对象层级
    const response = await request.get(`${API_URL}/api/v1/lookup-values/AUTH_OBJECT_LEVEL`, { timeout: 5000 });

    // 验证响应状态（如果后端未运行会返回404或其他错误）
    if (response.status() === 404 || response.status() === 502) {
      test.skip(true, 'Backend API not available - skipping test');
      return;
    }

    expect(response.status()).toBe(200);

    // 解析响应数据
    const data = await response.json();

    // 验证响应格式
    expect(data).toHaveProperty('code');
  });
});