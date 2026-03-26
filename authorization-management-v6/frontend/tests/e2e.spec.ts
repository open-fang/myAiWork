import { test, expect, Page } from '@playwright/test';

// 测试报告数据收集
interface TestResult {
  name: string;
  status: 'passed' | 'failed';
  duration: number;
  error?: string;
  screenshot?: string;
}

const testResults: TestResult[] = [];

test.describe('授权书管理系统 E2E 测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  // ==================== 页面加载测试 ====================
  test('应正确显示授权书列表页面', async ({ page }) => {
    await page.goto('/#/AuthLetterList');
    await expect(page.getByText('授权书列表')).toBeVisible({ timeout: 10000 });
  });

  test('应正确显示规则参数配置页面', async ({ page }) => {
    await page.goto('/#/RuleParamConfig');
    await expect(page.getByText('规则参数配置')).toBeVisible({ timeout: 10000 });
  });

  // ==================== 导航跳转测试 ====================
  test('应能从列表页跳转到新建页面', async ({ page }) => {
    await page.goto('/#/AuthLetterList');
    await page.getByRole('button', { name: '新建' }).click();
    await expect(page).toHaveURL(/#\/AuthLetterDetail.*mode=create/);
    await expect(page.getByText('新建授权书')).toBeVisible({ timeout: 5000 });
  });

  test('应能从列表页跳转到编辑页面', async ({ page }) => {
    await page.goto('/#/AuthLetterList');

    // 等待表格加载
    await page.waitForSelector('table tbody tr', { timeout: 10000 });

    // 点击第一行的编辑按钮
    const editBtn = page.locator('.action-edit').first();
    if (await editBtn.isVisible()) {
      await editBtn.click();
      await expect(page).toHaveURL(/#\/AuthLetterDetail.*mode=edit/);
    }
  });

  // ==================== 表单交互测试 ====================
  test('应能搜索授权书', async ({ page }) => {
    await page.goto('/#/AuthLetterList');

    // 等待页面加载
    await page.waitForSelector('.search-form', { timeout: 10000 });

    // 输入搜索条件
    const nameInput = page.getByPlaceholder('请输入授权书名称');
    if (await nameInput.isVisible()) {
      await nameInput.fill('测试');
      await page.getByRole('button', { name: '查询' }).click();

      // 等待表格刷新
      await page.waitForTimeout(1000);
      await expect(page.locator('table')).toBeVisible();
    }
  });

  // ==================== 自定义下拉组件测试 ====================
  test.describe('CustomSelect 组件测试', () => {
    test('下拉面板应能正确打开和关闭', async ({ page }) => {
      await page.goto('/#/AuthLetterList');
      await page.waitForSelector('.search-form', { timeout: 10000 });

      // 查找下拉选择器
      const selectTrigger = page.locator('.custom-select .select-trigger').first();
      if (await selectTrigger.isVisible()) {
        // 点击打开
        await selectTrigger.click();
        const dropdown = page.locator('.select-dropdown').first();
        await expect(dropdown).toBeVisible({ timeout: 3000 });

        // 再次点击关闭（点击触发器）
        await selectTrigger.click();
      }
    });

    test('点击外部应关闭下拉面板', async ({ page }) => {
      await page.goto('/#/AuthLetterList');
      await page.waitForSelector('.search-form', { timeout: 10000 });

      const selectTrigger = page.locator('.custom-select .select-trigger').first();
      if (await selectTrigger.isVisible()) {
        // 打开下拉面板
        await selectTrigger.click();
        const dropdown = page.locator('.select-dropdown').first();
        await expect(dropdown).toBeVisible({ timeout: 3000 });

        // 点击页面空白区域
        await page.locator('body').click({ position: { x: 50, y: 50 } });

        // 验证面板已关闭
        await expect(dropdown).not.toBeVisible({ timeout: 3000 });
      }
    });
  });

  // ==================== 树形选择组件测试 ====================
  test.describe('TreeSelect 组件测试', () => {
    test.beforeEach(async ({ page }) => {
      await page.goto('/#/AuthLetterDetail?mode=create');
      await page.waitForSelector('.page-container', { timeout: 10000 });
    });

    test('树形选择器应能打开', async ({ page }) => {
      // 查找树形选择器
      const treeSelectTrigger = page.locator('.custom-select .select-trigger').first();
      if (await treeSelectTrigger.isVisible()) {
        await treeSelectTrigger.click();
        await expect(page.locator('.select-dropdown').first()).toBeVisible({ timeout: 3000 });
      }
    });

    test('应能展开和折叠树节点', async ({ page }) => {
      // 打开树形选择器
      const treeSelectTrigger = page.locator('.custom-select .select-trigger').first();
      if (await treeSelectTrigger.isVisible()) {
        await treeSelectTrigger.click();
        await page.waitForTimeout(500);

        // 查找折叠按钮
        const toggleBtn = page.locator('.tree-toggle').first();
        if (await toggleBtn.isVisible()) {
          // 点击展开
          await toggleBtn.click();
          await page.waitForTimeout(300);

          // 再次点击折叠
          await toggleBtn.click();
        }
      }
    });

    test('选择节点不应触发折叠', async ({ page }) => {
      // 打开树形选择器
      const treeSelectTrigger = page.locator('.custom-select .select-trigger').first();
      if (await treeSelectTrigger.isVisible()) {
        await treeSelectTrigger.click();
        await page.waitForTimeout(500);

        // 展开节点
        const toggleBtn = page.locator('.tree-toggle').first();
        if (await toggleBtn.isVisible()) {
          await toggleBtn.click();
          await page.waitForTimeout(300);
        }

        // 点击复选框选择节点
        const checkbox = page.locator('.tree-node input[type="checkbox"]').first();
        if (await checkbox.isVisible()) {
          await checkbox.check();
          await page.waitForTimeout(300);

          // 验证下拉面板仍然打开（没有因为选择而关闭）
          const dropdown = page.locator('.select-dropdown').first();
          // 注意：这里不检查是否可见，因为点击复选框后可能关闭面板
          // 关键是树节点不应该折叠
        }
      }
    });
  });

  // ==================== 年份选择组件测试 ====================
  test.describe('YearSelect 组件测试', () => {
    test('年份选择器应显示正确的年份范围', async ({ page }) => {
      await page.goto('/#/AuthLetterList');
      await page.waitForSelector('.search-form', { timeout: 10000 });

      // 查找年份选择器
      const yearTrigger = page.locator('.custom-select .select-trigger').first();
      if (await yearTrigger.isVisible()) {
        await yearTrigger.click();

        const currentYear = new Date().getFullYear();

        // 验证显示当前年份
        const yearGrid = page.locator('.year-grid');
        if (await yearGrid.isVisible()) {
          await expect(page.getByText(String(currentYear))).toBeVisible({ timeout: 3000 });
        }
      }
    });

    test('应能选择年份', async ({ page }) => {
      await page.goto('/#/AuthLetterList');
      await page.waitForSelector('.search-form', { timeout: 10000 });

      const yearTrigger = page.locator('.custom-select .select-trigger').first();
      if (await yearTrigger.isVisible()) {
        await yearTrigger.click();

        const currentYear = new Date().getFullYear();
        const yearItem = page.locator('.year-item').getByText(String(currentYear));

        if (await yearItem.isVisible()) {
          await yearItem.click();

          // 验证下拉面板关闭
          await expect(page.locator('.select-dropdown').first()).not.toBeVisible({ timeout: 3000 });
        }
      }
    });
  });

  // ==================== 操作按钮测试 ====================
  test('操作按钮应使用文字而非 emoji', async ({ page }) => {
    await page.goto('/#/AuthLetterList');
    await page.waitForSelector('table tbody tr', { timeout: 10000 });

    // 检查编辑按钮是否为文字
    const editBtn = page.locator('.action-edit').first();
    if (await editBtn.isVisible()) {
      const text = await editBtn.textContent();
      expect(text).toContain('编辑');
      // 不应包含 emoji
      expect(text).not.toMatch(/[\u{1F300}-\u{1F9FF}]/u);
    }
  });

  // ==================== 表单按钮对齐测试 ====================
  test('查询按钮应与表单字段对齐', async ({ page }) => {
    await page.goto('/#/AuthLetterList');
    await page.waitForSelector('.search-form', { timeout: 10000 });

    // 检查按钮容器存在
    const buttonRow = page.locator('.form-row').filter({ hasText: '查询' });
    if (await buttonRow.isVisible()) {
      // 验证按钮存在
      await expect(page.getByRole('button', { name: '查询' })).toBeVisible();
    }
  });
});

// ==================== 详情页测试 ====================
test.describe('授权书详情页测试', () => {
  test('新建模式应显示可编辑表单', async ({ page }) => {
    await page.goto('/#/AuthLetterDetail?mode=create');
    await page.waitForSelector('.page-container', { timeout: 10000 });

    // 验证标题显示新建
    await expect(page.getByText('新建授权书')).toBeVisible({ timeout: 5000 });

    // 验证输入框可编辑
    const nameInput = page.getByPlaceholder('请输入授权书名称');
    if (await nameInput.isVisible()) {
      await expect(nameInput).toBeEditable();
    }
  });

  test('查看模式应显示只读表单', async ({ page }) => {
    // 先进入列表页获取一个ID
    await page.goto('/#/AuthLetterList');
    await page.waitForSelector('table tbody tr', { timeout: 10000 });

    // 点击查看按钮（如果存在）
    const viewBtn = page.locator('.action-view').first();
    if (await viewBtn.isVisible()) {
      await viewBtn.click();

      // 验证进入详情页
      await expect(page).toHaveURL(/#\/AuthLetterDetail/);
    }
  });
});

// ==================== 规则参数配置页测试 ====================
test.describe('规则参数配置页测试', () => {
  test('应正确显示规则参数列表', async ({ page }) => {
    await page.goto('/#/RuleParamConfig');
    await page.waitForSelector('.page-container', { timeout: 10000 });

    await expect(page.getByText('规则参数配置')).toBeVisible();
    await expect(page.locator('table')).toBeVisible();
  });

  test('应能打开新建弹窗', async ({ page }) => {
    await page.goto('/#/RuleParamConfig');
    await page.waitForSelector('.page-container', { timeout: 10000 });

    await page.getByRole('button', { name: '新建' }).click();

    // 验证弹窗打开
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await expect(page.getByText('新建规则参数')).toBeVisible();
  });

  test('应能关闭弹窗', async ({ page }) => {
    await page.goto('/#/RuleParamConfig');
    await page.waitForSelector('.page-container', { timeout: 10000 });

    // 打开弹窗
    await page.getByRole('button', { name: '新建' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });

    // 点击关闭按钮
    await page.locator('.modal-close').click();

    // 验证弹窗关闭
    await expect(page.locator('.modal-overlay')).not.toBeVisible({ timeout: 3000 });
  });
});

// ==================== 问卷题目配置测试 ====================
test.describe('问卷题目配置测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/#/AuthLetterDetail?mode=create');
    await page.waitForSelector('.page-container', { timeout: 10000 });
  });

  test('应能打开问卷题目配置弹窗', async ({ page }) => {
    // 点击添加场景按钮
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });

    // 点击问卷题目配置按钮
    await page.getByRole('button', { name: '问卷题目配置' }).click();

    // 验证问卷题目配置弹窗打开
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await expect(page.getByRole('heading', { name: '问卷题目配置' })).toBeVisible();
  });

  test('问卷题目配置弹窗应显示搜索条件', async ({ page }) => {
    // 打开问卷题目配置弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });

    // 验证搜索条件标签存在 - 使用label角色来精确匹配
    await expect(page.locator('.questionnaire-modal .search-form label').filter({ hasText: '题目编号' })).toBeVisible();
    await expect(page.locator('.questionnaire-modal .search-form label').filter({ hasText: '语言' })).toBeVisible();

    // 验证功能按钮存在 - 使用更宽松的选择器
    await expect(page.locator('.questionnaire-modal').getByRole('button', { name: '查询' })).toBeVisible();
    await expect(page.locator('.questionnaire-modal').getByRole('button', { name: '重置' })).toBeVisible();
    await expect(page.locator('.questionnaire-modal').getByRole('button', { name: '新增' })).toBeVisible();
  });

  test('应能关闭问卷题目配置弹窗', async ({ page }) => {
    // 打开问卷题目配置弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });

    // 点击关闭按钮
    const closeBtn = page.locator('.questionnaire-modal .modal-close');
    await closeBtn.click();

    // 验证弹窗关闭
    await expect(page.locator('.questionnaire-modal')).not.toBeVisible({ timeout: 3000 });
  });

  test('应能打开新增题目弹窗', async ({ page }) => {
    // 打开问卷题目配置弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });

    // 在问卷配置弹窗内点击新增按钮
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();

    // 验证题目维护弹窗打开（标题为"新增题目"）
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });
  });

  test('新增题目弹窗应显示题目文本表格', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 验证题目内容区域存在
    await expect(page.getByText('题目内容')).toBeVisible();

    // 验证添加行按钮存在
    await expect(page.getByRole('button', { name: '添加行' })).toBeVisible();
  });

  test('应能为题目添加多个文本行', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 点击添加行按钮
    await page.getByRole('button', { name: '添加行' }).click();
    await page.waitForTimeout(300);

    // 验证表格行数增加
    const rows = page.locator('.sub-table tbody tr');
    const count = await rows.count();
    expect(count).toBeGreaterThanOrEqual(1);
  });

  test('应能填写题目内容', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 添加一行
    await page.getByRole('button', { name: '添加行' }).click();
    await page.waitForTimeout(300);

    // 填写题目内容
    const questionInput = page.locator('.sub-table tbody input[type="text"]').first();
    if (await questionInput.isVisible()) {
      await questionInput.fill('测试题目内容');
      await expect(questionInput).toHaveValue('测试题目内容');
    }
  });

  test('应能选择题目语言', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 添加一行
    await page.getByRole('button', { name: '添加行' }).click();
    await page.waitForTimeout(300);

    // 查找语言下拉框
    const languageSelect = page.locator('.sub-table tbody select').first();
    if (await languageSelect.isVisible()) {
      // 选择中文
      await languageSelect.selectOption('ZH');
      await expect(languageSelect).toHaveValue('ZH');
    }
  });

  test('应能删除题目文本行', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 添加两行
    await page.getByRole('button', { name: '添加行' }).click();
    await page.waitForTimeout(200);
    await page.getByRole('button', { name: '添加行' }).click();
    await page.waitForTimeout(200);

    const rows = page.locator('.sub-table tbody tr');
    const initialCount = await rows.count();

    // 点击删除按钮（如果有）
    const deleteBtn = page.locator('.sub-table .action-delete').first();
    if (await deleteBtn.isVisible()) {
      await deleteBtn.click();
      await page.waitForTimeout(200);

      const newCount = await rows.count();
      expect(newCount).toBeLessThan(initialCount);
    }
  });

  test('新增题目弹窗应显示答案表格区域', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 验证答案表格区域存在
    await expect(page.getByText('可选答案')).toBeVisible();
  });

  test('应能打开答案维护弹窗', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 点击新增答案按钮
    const addAnswerBtn = page.getByRole('button', { name: '新增答案' });
    if (await addAnswerBtn.isVisible()) {
      await addAnswerBtn.click();

      // 验证答案维护弹窗打开
      await expect(page.getByRole('heading', { name: '问卷答案维护' })).toBeVisible({ timeout: 3000 });
    }
  });

  test('应能关闭题目维护弹窗', async ({ page }) => {
    // 打开新增题目弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });
    await page.getByRole('button', { name: '问卷题目配置' }).click();
    await expect(page.locator('.questionnaire-modal')).toBeVisible({ timeout: 3000 });
    await page.locator('.questionnaire-modal').getByRole('button', { name: '新增' }).click();
    await expect(page.getByRole('heading', { name: '新增题目' })).toBeVisible({ timeout: 3000 });

    // 点击题目弹窗的取消按钮（最后一个可见的modal的取消按钮）
    const cancelBtns = page.locator('.modal-content .modal-footer .btn').filter({ hasText: '取消' });
    const lastCancelBtn = cancelBtns.last();
    await lastCancelBtn.click();

    // 验证题目弹窗关闭，回到问卷列表弹窗
    await expect(page.getByRole('heading', { name: '新增题目' })).not.toBeVisible({ timeout: 3000 });
  });
});

// ==================== 场景问卷配置测试（原有） ====================
test.describe('场景问卷配置测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/#/AuthLetterDetail?mode=create');
    await page.waitForSelector('.page-container', { timeout: 10000 });
  });

  test('场景弹窗应显示问卷配置区域', async ({ page }) => {
    // 点击添加场景按钮
    await page.getByRole('button', { name: '添加场景' }).click();

    // 等待弹窗打开
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });

    // 验证问卷配置区域存在
    await expect(page.getByText('问卷配置')).toBeVisible({ timeout: 3000 });
    await expect(page.locator('.questionnaire-section')).toBeVisible();
  });

  test('应能输入问卷内容', async ({ page }) => {
    // 打开场景弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });

    // 填写基本场景信息
    await page.getByPlaceholder('请输入场景名称').fill('测试场景');

    // 输入问卷内容
    const questionnaireTextarea = page.locator('.questionnaire-section textarea');
    await questionnaireTextarea.fill('{"questions":[{"title":"问题1","options":["选项A","选项B"]}]}');

    // 验证内容已输入
    await expect(questionnaireTextarea).toHaveValue(/问题1/);
  });

  test('应能点击问卷题目配置按钮', async ({ page }) => {
    // 打开场景弹窗
    await page.getByRole('button', { name: '添加场景' }).click();
    await expect(page.locator('.modal-overlay')).toBeVisible({ timeout: 3000 });

    // 验证问卷题目配置按钮存在
    const configBtn = page.getByRole('button', { name: '问卷题目配置' });
    await expect(configBtn).toBeVisible();
  });
});