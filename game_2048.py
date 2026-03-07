import pygame
import random
import sys

# 初始化pygame
pygame.init()

# 游戏常量
GRID_SIZE = 4
CELL_SIZE = 100
PADDING = 10
WINDOW_WIDTH = GRID_SIZE * CELL_SIZE + (GRID_SIZE + 1) * PADDING
WINDOW_HEIGHT = WINDOW_WIDTH + 80  # 额外空间用于分数显示
FPS = 60

# 颜色定义
BACKGROUND_COLOR = (187, 173, 160)
GRID_COLOR = (205, 193, 180)
EMPTY_CELL_COLOR = (205, 193, 180)
TEXT_COLOR = (119, 110, 101)
SCORE_BG_COLOR = (187, 173, 160)
SCORE_TEXT_COLOR = (249, 246, 242)
GAME_OVER_BG = (238, 228, 218, 180)
GAME_OVER_TEXT = (119, 110, 101)

# 数字颜色映射
NUMBER_COLORS = {
    0: (205, 193, 180),
    2: (238, 228, 218),
    4: (237, 224, 200),
    8: (242, 177, 121),
    16: (245, 149, 99),
    32: (246, 124, 95),
    64: (246, 94, 59),
    128: (237, 207, 114),
    256: (237, 204, 97),
    512: (237, 200, 80),
    1024: (237, 197, 63),
    2048: (237, 194, 46)
}

# 数字文本颜色映射
TEXT_COLORS = {
    2: (119, 110, 101),
    4: (119, 110, 101),
    8: (249, 246, 242),
    16: (249, 246, 242),
    32: (249, 246, 242),
    64: (249, 246, 242),
    128: (249, 246, 242),
    256: (249, 246, 242),
    512: (249, 246, 242),
    1024: (249, 246, 242),
    2048: (249, 246, 242)
}

class Game2048:
    def __init__(self):
        self.screen = pygame.display.set_mode((WINDOW_WIDTH, WINDOW_HEIGHT))
        pygame.display.set_caption("2048 Game")
        self.clock = pygame.time.Clock()
        self.font = pygame.font.SysFont(None, 48)
        self.small_font = pygame.font.SysFont(None, 36)
        self.score_font = pygame.font.SysFont(None, 32)
        self.reset_game()
    
    def reset_game(self):
        """重置游戏状态"""
        self.grid = [[0 for _ in range(GRID_SIZE)] for _ in range(GRID_SIZE)]
        self.score = 0
        self.game_over = False
        # 初始生成两个数字
        self.add_random_tile()
        self.add_random_tile()
    
    def add_random_tile(self):
        """在空白位置随机添加一个数字（90%概率为2，10%概率为4）"""
        empty_cells = [(r, c) for r in range(GRID_SIZE) for c in range(GRID_SIZE) if self.grid[r][c] == 0]
        if empty_cells:
            row, col = random.choice(empty_cells)
            self.grid[row][col] = 2 if random.random() < 0.9 else 4
    
    def add_random_tile_optimized(self, direction):
        """优化版：在空白位置随机添加数字，避免在大数正下方或正右方"""
        empty_cells = [(r, c) for r in range(GRID_SIZE) for c in range(GRID_SIZE) if self.grid[r][c] == 0]
        
        if not empty_cells:
            return
        
        # 找到当前最大数字的位置
        max_value = 0
        max_positions = []
        for r in range(GRID_SIZE):
            for c in range(GRID_SIZE):
                if self.grid[r][c] > max_value:
                    max_value = self.grid[r][c]
                    max_positions = [(r, c)]
                elif self.grid[r][c] == max_value and max_value > 0:
                    max_positions.append((r, c))
        
        # 如果向左或向上移动，尝试避免在大数正下方或正右方生成新数字
        if direction in ['left', 'up'] and max_positions:
            # 找出需要避免的位置
            avoid_positions = []
            for r, c in max_positions:
                # 大数的正下方（如果存在）
                if r + 1 < GRID_SIZE and self.grid[r + 1][c] == 0:
                    avoid_positions.append((r + 1, c))
                # 大数的正右方（如果存在）
                if c + 1 < GRID_SIZE and self.grid[r][c + 1] == 0:
                    avoid_positions.append((r, c + 1))
            
            # 从可选位置中移除需要避免的位置
            safe_cells = [cell for cell in empty_cells if cell not in avoid_positions]
            
            # 如果有安全位置，优先选择安全位置
            if safe_cells:
                row, col = random.choice(safe_cells)
            else:
                # 如果没有安全位置，使用所有空白位置
                row, col = random.choice(empty_cells)
        else:
            # 其他方向或没有大数，随机选择
            row, col = random.choice(empty_cells)
        
        self.grid[row][col] = 2 if random.random() < 0.9 else 4
    
    def move(self, direction):
        """移动网格中的数字"""
        if self.game_over:
            return False
        
        moved = False
        score_added = 0
        
        # 根据方向处理移动
        if direction == 'left':
            for row in range(GRID_SIZE):
                # 移除空格
                non_zero = [cell for cell in self.grid[row] if cell != 0]
                # 合并相同数字
                merged = []
                i = 0
                while i < len(non_zero):
                    if i + 1 < len(non_zero) and non_zero[i] == non_zero[i + 1]:
                        merged.append(non_zero[i] * 2)
                        score_added += non_zero[i] * 2
                        i += 2
                    else:
                        merged.append(non_zero[i])
                        i += 1
                # 填充剩余位置
                new_row = merged + [0] * (GRID_SIZE - len(merged))
                if self.grid[row] != new_row:
                    moved = True
                self.grid[row] = new_row
        
        elif direction == 'right':
            for row in range(GRID_SIZE):
                non_zero = [cell for cell in self.grid[row] if cell != 0]
                merged = []
                i = len(non_zero) - 1
                while i >= 0:
                    if i - 1 >= 0 and non_zero[i] == non_zero[i - 1]:
                        merged.insert(0, non_zero[i] * 2)
                        score_added += non_zero[i] * 2
                        i -= 2
                    else:
                        merged.insert(0, non_zero[i])
                        i -= 1
                new_row = [0] * (GRID_SIZE - len(merged)) + merged
                if self.grid[row] != new_row:
                    moved = True
                self.grid[row] = new_row
        
        elif direction == 'up':
            for col in range(GRID_SIZE):
                column = [self.grid[row][col] for row in range(GRID_SIZE)]
                non_zero = [cell for cell in column if cell != 0]
                merged = []
                i = 0
                while i < len(non_zero):
                    if i + 1 < len(non_zero) and non_zero[i] == non_zero[i + 1]:
                        merged.append(non_zero[i] * 2)
                        score_added += non_zero[i] * 2
                        i += 2
                    else:
                        merged.append(non_zero[i])
                        i += 1
                new_column = merged + [0] * (GRID_SIZE - len(merged))
                for row in range(GRID_SIZE):
                    if self.grid[row][col] != new_column[row]:
                        moved = True
                    self.grid[row][col] = new_column[row]
        
        elif direction == 'down':
            for col in range(GRID_SIZE):
                column = [self.grid[row][col] for row in range(GRID_SIZE)]
                non_zero = [cell for cell in column if cell != 0]
                merged = []
                i = len(non_zero) - 1
                while i >= 0:
                    if i - 1 >= 0 and non_zero[i] == non_zero[i - 1]:
                        merged.insert(0, non_zero[i] * 2)
                        score_added += non_zero[i] * 2
                        i -= 2
                    else:
                        merged.insert(0, non_zero[i])
                        i -= 1
                new_column = [0] * (GRID_SIZE - len(merged)) + merged
                for row in range(GRID_SIZE):
                    if self.grid[row][col] != new_column[row]:
                        moved = True
                    self.grid[row][col] = new_column[row]
        
        # 如果移动了，添加新数字并更新分数
        if moved:
            self.score += score_added
            self.add_random_tile_optimized(direction)
            self.check_game_over()
        
        return moved
    
    def check_game_over(self):
        """检查游戏是否结束"""
        # 检查是否有空格
        for row in range(GRID_SIZE):
            for col in range(GRID_SIZE):
                if self.grid[row][col] == 0:
                    return False
        
        # 检查是否有可合并的相邻数字
        for row in range(GRID_SIZE):
            for col in range(GRID_SIZE):
                current = self.grid[row][col]
                # 检查右侧
                if col + 1 < GRID_SIZE and self.grid[row][col + 1] == current:
                    return False
                # 检查下方
                if row + 1 < GRID_SIZE and self.grid[row + 1][col] == current:
                    return False
        
        self.game_over = True
        return True
    
    def draw(self):
        """绘制游戏界面"""
        # 绘制背景
        self.screen.fill(BACKGROUND_COLOR)
        
        # 绘制网格
        for row in range(GRID_SIZE):
            for col in range(GRID_SIZE):
                # 计算单元格位置
                x = col * CELL_SIZE + (col + 1) * PADDING
                y = row * CELL_SIZE + (row + 1) * PADDING
                
                # 绘制单元格背景
                cell_value = self.grid[row][col]
                cell_color = NUMBER_COLORS.get(cell_value, NUMBER_COLORS[0])
                pygame.draw.rect(self.screen, cell_color, (x, y, CELL_SIZE, CELL_SIZE), border_radius=6)
                
                # 绘制数字
                if cell_value != 0:
                    text_color = TEXT_COLORS.get(cell_value, TEXT_COLORS[2048])
                    text = self.font.render(str(cell_value), True, text_color)
                    text_rect = text.get_rect(center=(x + CELL_SIZE // 2, y + CELL_SIZE // 2))
                    self.screen.blit(text, text_rect)
        
        # 绘制分数
        score_bg_rect = pygame.Rect(PADDING, WINDOW_HEIGHT - 70, WINDOW_WIDTH - 2 * PADDING, 60)
        pygame.draw.rect(self.screen, SCORE_BG_COLOR, score_bg_rect, border_radius=6)
        
        score_text = self.score_font.render(f"Score: {self.score}", True, SCORE_TEXT_COLOR)
        score_rect = score_text.get_rect(center=score_bg_rect.center)
        self.screen.blit(score_text, score_rect)
        
        # 绘制游戏结束提示
        if self.game_over:
            overlay = pygame.Surface((WINDOW_WIDTH, WINDOW_HEIGHT - 80), pygame.SRCALPHA)
            overlay.fill(GAME_OVER_BG)
            self.screen.blit(overlay, (0, 0))
            
            game_over_text = self.font.render("Game Over!", True, GAME_OVER_TEXT)
            restart_text = self.small_font.render("Press R to restart", True, GAME_OVER_TEXT)
            
            game_over_rect = game_over_text.get_rect(center=(WINDOW_WIDTH // 2, WINDOW_HEIGHT // 2 - 100))
            restart_rect = restart_text.get_rect(center=(WINDOW_WIDTH // 2, WINDOW_HEIGHT // 2 - 40))
            
            self.screen.blit(game_over_text, game_over_rect)
            self.screen.blit(restart_text, restart_rect)
        
        pygame.display.flip()
    
    def run(self):
        """运行游戏主循环"""
        running = True
        
        while running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False
                elif event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_r:
                        self.reset_game()
                    elif not self.game_over:
                        if event.key == pygame.K_LEFT:
                            self.move('left')
                        elif event.key == pygame.K_RIGHT:
                            self.move('right')
                        elif event.key == pygame.K_UP:
                            self.move('up')
                        elif event.key == pygame.K_DOWN:
                            self.move('down')
            
            self.draw()
            self.clock.tick(FPS)
        
        pygame.quit()
        sys.exit()

if __name__ == "__main__":
    game = Game2048()
    game.run()