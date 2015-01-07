require(ggplot2)

## README

# Change the following two paths to the input path of where ScalaMeter stored the *dsv result files...
inputPath <- "~/Dropbox/TUD/Thesis/code/join/tmp/"
# ... and where you would like the script to output the graphs:
outputPath <- "/Users/ayedo/Dropbox/TUD/Thesis/document/img/evaluation/"
# To run the script use Rscript GeneratePlot.r, or use RStudio. When using RScript you might want to fix the
# size of the plots by changing the ggsave command options to a fixed size.


# Drops columns which are not required
processed_import <- function(path, name) {
  dt <- read.csv(path, stringsAsFactors=FALSE)
  cropped <- subset(dt, select=-c(units, success))
  cropped$Approach = name
  return(cropped)
}

rxj <- processed_import(paste0(inputPath, "twoCasesIndependend.ReactiveX.dsv"), "RXJ")
nct <- processed_import(paste0(inputPath, "twoCasesIndependend.Non-Deterministic Choice.dsv"), "NCT")
dct <- processed_import(paste0(inputPath, "twoCasesIndependend.Deterministic Choice.dsv"), "DCT")

rxj <- rxj[1:4, ]
nct <- nct[1:4, ]
dct <- dct[1:4, ]

all = rbind(rxj, nct, dct)

# Rename the param.Observables column
names(all)[names(all)=="param.Observables"] <- "observables"
all$observables <- factor(all$observables)

# The number of events sent per observable
sentEvents = 32768

# 1. Two Cases Independent: twice number of events sent per observable. Constant.
patternMatches = 2 * sentEvents

valuet = as.numeric(lapply(all$value, function(x) patternMatches / (x / 1000)))
fprime = as.numeric(lapply(all$value, function(m) (-patternMatches * 1000) / (m ** 2)))
diff = all$cihi - all$value
variance = as.numeric(lapply(diff, function(d) (d / 6.361) ** 2))
variancet = variance * as.numeric(lapply(fprime, function(x) x ** 2))
all$value = valuet
all$cilo = valuet - 6.361 * sqrt(variancet)
all$cihi = valuet + 6.361 * sqrt(variancet)

rxjValues = all[which(all$Approach=='RXJ'), ]$value
nctValues = all[which(all$Approach=='NCT'), ]$value

1-mean(rxjValues / nctValues)

ggplot(all, aes(x=observables, y=value, fill=Approach)) + 
    geom_bar(position=position_dodge(), stat="identity",
            colour="black") +
    geom_errorbar(aes(ymin=cilo, ymax=cihi),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9)) +
    xlab("Observables") +
    ylab("Throughput (matches/s)") + 
    scale_fill_hue(name="Approach", # Legend label, use darker colors
                   breaks=c("DCT", "NCT", "RXJ"),
                   labels=c("Deterministic Choice Transform", 
                    "Non-Deterministic Choice Transform", 
                    "Reactive Extensions")) +
    ggtitle("Throughput with Increasing Size of Compositions") +
    theme_bw() +
    theme(plot.title = element_text(size=32, face="bold", vjust=2)) +
    theme(axis.title.y=element_text(face="bold", vjust=1)) + 
    theme(axis.title.x=element_text(face="bold", vjust=0)) +
    theme(legend.position="bottom",  panel.grid.major.x = element_blank() ,
           panel.grid.major.y = element_line( size=.1, color="black")) + 
    scale_fill_manual(values=c("#CC6666", "#9999CC", "#66CC99"))
ggsave(file=paste0(outputPath, "TwoChoiceNObservables.eps"))

# 2. N Case Independent: number of events sent per observable * number of cases

rxj <- processed_import(paste0(inputPath, "NCaseTwoIndependent.ReactiveX.dsv"), "RXJ")
nct <- processed_import(paste0(inputPath, "NCaseTwoIndependent.Non-Deterministic Choice.dsv"), "NCT")
dct <- processed_import(paste0(inputPath, "NCaseTwoIndependent.Deterministic Choice.dsv"), "DCT")

all = rbind(rxj, nct, dct)

names(all)[names(all)=="param.Choices"] <- "choices"

patternMatches = all$choices * sentEvents
all$choices <- factor(all$choices)
valuet = patternMatches / (all$value / 1000)
fprime = (-patternMatches * 1000) / (all$value ** 2)
diff = all$cihi - all$value
variance = (diff / 6.361) ** 2
variancet = variance * (fprime ** 2)
all$value = valuet
all$cilo = valuet - 6.361 * sqrt(variancet)
all$cihi = valuet + 6.361 * sqrt(variancet)

rxjValues = all[which(all$Approach=='RXJ'), ]$value
nctValues = all[which(all$Approach=='NCT'), ]$value

1-mean(rxjValues / nctValues)

ggplot(all, aes(x=choices, y=value, fill=Approach)) + 
    geom_bar(position=position_dodge(), stat="identity",
            colour="black") +
    geom_errorbar(aes(ymin=cilo, ymax=cihi),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9)) +
    xlab("Choices") +
    ylab("Throughput (matches/s)") + 
    scale_fill_hue(name="Approach", # Legend label, use darker colors
                   breaks=c("DCT", "NCT", "RXJ"),
                   labels=c("Deterministic Choice Transform", 
                    "Non-Deterministic Choice Transform", 
                    "Reactive Extensions")) +
    ggtitle("Throughput with Increasing Number of Choices") +
    theme_bw() + 
    theme(plot.title = element_text(size=32, face="bold", vjust=2)) +
    theme(axis.title.y=element_text(face="bold", vjust=1)) + 
    theme(axis.title.x=element_text(face="bold", vjust=0))  +
    theme(legend.position="bottom",  panel.grid.major.x = element_blank() ,
           panel.grid.major.y = element_line( size=.1, color="black" )) +
    scale_fill_manual(values=c("#CC6666", "#9999CC", "#66CC99"))
ggsave(paste0(outputPath, "NChoiceTwoObservables.eps"))

# 3. 16 Case N Dependent: 16 * number of events sent per observable
rxj <- processed_import(paste0(inputPath, "NDependentCases.ReactiveX.dsv"), "RXJ")
nct <- processed_import(paste0(inputPath, "NDependentCases.Non-Deterministic Choice.dsv"), "NCT")
dct <- processed_import(paste0(inputPath, "NDependentCases.Deterministic Choice.dsv"), "DCT")

all = rbind(rxj, nct, dct)

names(all)[names(all)=="param.Choices"] <- "choices"

patternMatches = rep(16 * sentEvents, length(all$choices))

all$choices <- factor(all$choices)
valuet = patternMatches / (all$value / 1000)
fprime = (-patternMatches * 1000) / (all$value ** 2)
diff = all$cihi - all$value
variance = (diff / 6.361) ** 2
variancet = variance * (fprime ** 2)
all$value = valuet
all$cilo = valuet - 6.361 * sqrt(variancet)
all$cihi = valuet + 6.361 * sqrt(variancet)

rxjValues = all[which(all$Approach=='RXJ'), ]$value
nctValues = all[which(all$Approach=='NCT'), ]$value

1-mean(rxjValues / nctValues)

ggplot(all, aes(x=choices, y=value, fill=Approach)) + 
    geom_bar(position=position_dodge(), stat="identity",
            colour="black") +
    geom_errorbar(aes(ymin=cilo, ymax=cihi),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9)) +
    xlab("Choices") +
    ylab("Throughput (matches/s)") + 
    scale_fill_hue(name="Approach", # Legend label, use darker colors
                   breaks=c("DCT", "NCT", "RXJ"),
                   labels=c("Deterministic Choice Transform", 
                    "Non-Deterministic Choice Transform", 
                    "Reactive Extensions")) +
    ggtitle("Throughput with Increasing Size of Interdependentness") +
    theme_bw() +
    theme(plot.title = element_text(size=32, face="bold", vjust=2)) +
    theme(axis.title.y=element_text(face="bold", vjust=1)) + 
    theme(axis.title.x=element_text(face="bold", vjust=0)) +
    theme(legend.position="bottom",  panel.grid.major.x = element_blank() ,
           panel.grid.major.y = element_line( size=.1, color="black" )) + 
    scale_fill_manual(values=c("#CC6666", "#9999CC", "#66CC99"))
ggsave(paste0(outputPath, "32ChoiceNInterdependent.eps"))
