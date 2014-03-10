function [result] = number3(dic, code)

    D = load(dic);
    C = load(code);

    result = zeros(272,280);
    pixelvalue = zeros(1,76160);
    [n] = length(C.Code);
    s = '';
    counter = 1;
    
    for i = 1:n
        s = strcat(s,C.Code(i));
        for j = 1:150
            if(strcmp(D.Dictionary(j,2),s) == 1)
                pixelvalue(counter) = D.Dictionary{j,1};
                counter = counter + 1;
                s = '';
                break;
            end
        end
    end

    counter = 1;
    
    for i = 1:280
        for j = 1:272    
            result(i,j) = pixelvalue(counter);
            counter = counter + 1;
        end    
    end

    result = imshow(uint8(result));

end