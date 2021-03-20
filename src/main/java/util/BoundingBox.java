package util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
@AllArgsConstructor
public class BoundingBox {
    int topY;
    int bottomY;
    int leftX;
    int rightX;

    public List<Integer> getXRange() {
        List <Integer> xCoords = new ArrayList<>();
        IntStream.range(leftX, rightX).forEach(x -> {
            xCoords.add(x);
        });
        return xCoords;
    }

    public int getWidth() {
        return rightX - leftX;
    }

    public int getHeight() {
        return topY - bottomY;
    }

    public List<Integer> getYRange() {
        List <Integer> yCoords = new ArrayList<>();
        IntStream.range(bottomY, topY).forEach(y -> {
            yCoords.add(y);
        });
        return yCoords;
    }
}
