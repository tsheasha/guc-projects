function [corner,ch,cv,cd1,cd2] = cornerMoravec(I,border)
I = imread(I);
hori = [1 -1];
vert = hori';
diag1 = [1 0; 0 -1];
diag2 = [0 1; -1 0];


average = ones(4,4); % average filter


conv_type='same';


h = conv2(I,hori,conv_type);
v = conv2(I,vert,conv_type);
d1 = conv2(I,diag1,conv_type);
d2 = conv2(I,diag2,conv_type);


c=zeros(size(I));
for i=1:prod(size(I))
c(i) = min([h(i)^2 v(i)^2 d1(i)^2 d2(i)^2]);
end


for i=2:size(I,1)-1
for j=2:size(I,2)-1


hh = sum(sum(abs(h(i-1:i+1,j-1:j+1)))); % 3x3 subimage
vv = sum(sum(abs(v(i-1:i+1,j-1:j+1)))); % 3x3 subimage
dd1 = sum(sum(abs(d1(i-1:i+1,j-1:j+1)))); % 3x3 subimage
dd2 = sum(sum(abs(d2(i-1:i+1,j-1:j+1)))); % 3x3 subimage


c(i,j) = min(hh,min(vv,min(dd1,dd2)));
ch(i,j) = hh;
cv(i,j) = vv;
cd1(i,j) = dd1;
cd2(i,j) = dd2;


end
end


corner=c;
corner=corner(border+1:end-border,border+1:end-border);
ch = ch(border+1:end-border,border+1:end-border);
cv = cv(border+1:end-border,border+1:end-border);
cd1 = cd1(border+1:end-border,border+1:end-border);
cd2 = cd2(border+1:end-border,border+1:end-border);

imwrite(corner,strcat('Corner_',num2str(border),'.jpg'));
