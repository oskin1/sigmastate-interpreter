#!/usr/bin/env bash
pdflatex sigma
bibtext sigma
pdflatex sigma
pdflatex sigma
rm sigma.aux
rm sigma.log
rm sigma.out
