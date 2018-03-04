clear all;

N = 19;

black_stone_bad_contrast = rgb2gray(imread('board42black.jpg'));
white_stone_bad_contrast = rgb2gray(imread('board42white.jpg'));
board_bad_contrast = rgb2gray(imread('board42.jpg'));

black_stone = imadjust(black_stone_bad_contrast);
white_stone = imadjust(white_stone_bad_contrast);
board = imadjust(board_bad_contrast);

board_matrix = zeros(N,N);
dim_board = size(board);
length_x = dim_board(1)/(N-1);
length_y = dim_board(2)/(N-1);
board_coordinates = zeros(N,N,2);

c = normxcorr2(black_stone, board);
d = normxcorr2(white_stone, board);

c_max = max(c(:));
d_max = max(d(:));

[row, col] = find(c>c_max*0.70);
[row2, col2] = find(d>d_max*0.70);

for i=1:numel(row)
    for j=(i+1):numel(row)
        if abs(row(i)-row(j))<50 && abs(col(i)-col(j))<50
            row(j) = -1;
            col(j) = -1;
        end
    end
end

for i=1:numel(row2)
    for j=(i+1):numel(row2)
        if abs(row2(i)-row2(j))<50 && abs(col2(i)-col2(j))<50
            row2(j) = -1;
            col2(j) = -1;
        end
    end
end


COL = [];
ROW = [];
COL2 = [];
ROW2 = [];

for i=1:numel(row)
    if row(i) ~= -1
        COL = [COL; col(i)];
        ROW = [ROW; row(i)];
    end
end

for i=1:numel(row2)
    if row2(i) ~= -1
        COL2 = [COL2; col2(i)];
        ROW2 = [ROW2; row2(i)];
    end
end
            

rowOffSet = ROW-size(black_stone,1);
colOffSet = COL-size(black_stone,2);

rowOffSet2 = ROW2-size(white_stone,1);
colOffSet2 = COL2-size(white_stone,2);

figure;
imshow(board);

for i=1:numel(ROW)
    imrect(gca, [colOffSet(i)+1, rowOffSet(i)+1, size(black_stone,2), size(black_stone,1)]);
end

for i=1:numel(ROW2)
    imrect(gca, [colOffSet2(i)+1, rowOffSet2(i)+1, size(white_stone,2), size(white_stone,1)]);
end


