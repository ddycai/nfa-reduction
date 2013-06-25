#!/bin/sh
./randnfa $1 $2 > rand.nfa
./reduce-nfa rand.nfa
