"""Materiais compartilhados pela linha de cultivo: as mesmas rampas e pinturas do Garrafão."""
from __future__ import annotations

import machine_kit as k
from machine_kit import BRASS, COPPER, CYAN, GLASS, IVORY, LEATHER, MAGMA, PATINA, STEAM, STEEL


def brass(c):
    k.plate(c, BRASS, 3)
    k.rivets(c, BRASS, 8, 0)
    k.patina(c, ((1, c.h - 2),), 1)
    if c.w > 8 and c.h > 4:
        k.scratches(c, BRASS, 2)


def cord(c):
    k.cylinder(c, LEATHER, 3)
    for y in range(c.h):
        for x in range(c.w):
            if (x + y) % 5 == 0:
                c.put(x, y, LEATHER[1])
            elif (x + y) % 5 == 1:
                c.put(x, y, LEATHER[5])


def glass(c):
    c.fill((*GLASS[2][:3], 35))
    for y in range(c.h):
        for x in range(c.w):
            if x == 0 or y == 0:
                c.put(x, y, (*GLASS[5][:3], 155))
            elif x == c.w - 1 or y == c.h - 1:
                c.put(x, y, (*GLASS[0][:3], 140))
            elif (x + y) in (5, 6, 7, 18):
                c.put(x, y, (*GLASS[5][:3], 70))
    if c.h > 10:
        for y in range(5, c.h - 2, 4):
            c.put(c.w - 2, y, (*IVORY[4][:3], 180))

