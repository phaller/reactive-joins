require(ggplot2)

# Drops columns which are not required
processed_import <- function(path, name) {
  dt <- read.csv(path, stringsAsFactors=FALSE)
  cropped <- subset(dt, select=-c(complete, units, success, date))
  cropped$Approach = name
  return(cropped)
}

rx <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/twoCasesIndependend.ReactiveX.dsv", "Rx")
nct <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/twoCasesIndependend.Non-Deterministic Choice.dsv", "NCT")
dct <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/twoCasesIndependend.Deterministic Choice.dsv", "DCT")

all = rbind(rx, nct, dct)

# Rename the param.Observables column
names(all)[names(all)=="param.Observables"] <- "observables"
all$observables <- factor(all$observables)

# The number of events sent per observable
sentEvents = 16384

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

all[5,2:4] = 0

ggplot(all, aes(x=observables, y=value, fill=Approach)) + 
    geom_bar(position=position_dodge(), stat="identity",
            colour="black") +
    geom_errorbar(aes(ymin=cilo, ymax=cihi),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9)) +
    xlab("Observables") +
    ylab("Throughput (matches/s)") + 
    scale_fill_hue(name="Approach", # Legend label, use darker colors
                   breaks=c("DCT", "NCT", "Rx"),
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

ggsave(file="/Users/ayedo/Dropbox/TUD/Thesis/document/img/evaluation/TwoChoiceNObservables.eps")

# 2. N Case Independent: number of events sent per observable * number of cases

rx <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/NCaseTwoIndependent.ReactiveX.dsv", "Rx")
nct <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/NCaseTwoIndependent.Non-Deterministic Choice.dsv", "NCT")
dct <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/NCaseTwoIndependent.Deterministic Choice.dsv", "DCT")

all = rbind(rx, nct, dct)

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

ggplot(all, aes(x=choices, y=value, fill=Approach)) + 
    geom_bar(position=position_dodge(), stat="identity",
            colour="black") +
    geom_errorbar(aes(ymin=cilo, ymax=cihi),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9)) +
    xlab("Choices") +
    ylab("Throughput (matches/s)") + 
    scale_fill_hue(name="Approach", # Legend label, use darker colors
                   breaks=c("DCT", "NCT", "Rx"),
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

ggsave("/Users/ayedo/Dropbox/TUD/Thesis/document/img/evaluation/NChoiceTwoObservables.eps")

# 3. 32 Case N Dependent: 32 * number of events sent per observable
rx <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/NDependentCases.ReactiveX.dsv", "Rx")
nct <- processed_import("~/Dropbox/TUD/Thesis/code/join/tmp/NDependentCases.Non-Deterministic Choice.dsv", "NCT")

all = rbind(rx, nct)

names(all)[names(all)=="param.Choices"] <- "choices"

patternMatches = rep(32 * sentEvents, length(all$choices))

all$choices <- factor(all$choices)
valuet = patternMatches / (all$value / 1000)
fprime = (-patternMatches * 1000) / (all$value ** 2)
diff = all$cihi - all$value
variance = (diff / 6.361) ** 2
variancet = variance * (fprime ** 2)
all$value = valuet
all$cilo = valuet - 6.361 * sqrt(variancet)
all$cihi = valuet + 6.361 * sqrt(variancet)

ggplot(all, aes(x=choices, y=value, fill=Approach)) + 
    geom_bar(position=position_dodge(), stat="identity",
            colour="black") +
    geom_errorbar(aes(ymin=cilo, ymax=cihi),
                  width=.2,                    # Width of the error bars
                  position=position_dodge(.9)) +
    xlab("Choices") +
    ylab("Throughput (matches/s)") + 
    scale_fill_hue(name="Approach", # Legend label, use darker colors
                   breaks=c("DCT", "NCT", "Rx"),
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
    scale_fill_manual(values=c("#9999CC", "#66CC99"))

    ggsave("/Users/ayedo/Dropbox/TUD/Thesis/document/img/evaluation/32ChoiceNInterdependent.eps")
