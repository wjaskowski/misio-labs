#!/usr/bin/env Rscript
library("reshape2")
library("ggplot2")

# Mades kruskal test followed by pairwise wilcoxon test on a dataframe
# df columns should be sorted: higher median values first (TODO: This could be made automatically)
kruskal.wilcoxon <- function(df) {
    # Nonparametric probability that there is no difference in distributions of k algorithms.
    # Value lower than alpha means a statistical difference
    print(kruskal.test(as.list(df)))

    data = stack(df)
    values = data$values
    factors = factor(data$ind, colnames(df), ordered=TRUE)
    #print(pairwise.wilcox.test(values, factors, alternative='less'))
    pairwise.t.test(values, factors, alternative='less')
}

df = read.csv(file=file.path('results.csv'), check.names=FALSE, sep=',')

x = kruskal.wilcoxon(df)
options(width=10000)
print(x)
