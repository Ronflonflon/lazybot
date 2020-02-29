package com.lazybot.microservices.map.business;

import com.lazybot.microservices.map.model.Cell;
import com.lazybot.microservices.map.model.Position;

public class CellManager {
    public Cell convertBlockToCell(int block, int position, int ray) {
        Cell cell = new Cell();
        cell.setIdBlock(block / 16);

        return cell;
    }

    public Position findPosition(int positionBlock, int ray) {
        Position pos = new Position();
        int dimension = ray * 2 + 1;

        for (int i = 1; i <= dimension; i++) {
            if (positionBlock >= dimension * i - dimension && positionBlock <= dimension * i - 1) {
                pos.setX(i - 1);
                break;
            }
        }

        int z = 0;
        for (int i = 0; i < dimension * dimension; i++) {

        }
        pos.setZ(z);
        return pos;
    }
}
