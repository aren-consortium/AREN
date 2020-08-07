<template>
    <div class="D3_pie">
        <toggle-action-button
            ref="option"
            v-bind:off-label="$t('by_comment_count')"
            v-bind:on-label="$t('by_letter_count')"
            v-model="byLetter">
        </toggle-action-button>
        <toggle-action-button
            v-bind:off-label="$t('by_user')"
            v-bind:on-label="$t('by_opinion')"
            v-model="byOpinion">
        </toggle-action-button>
        <svg ref="svg" viewBox="0 0 1 1">
            <g class="slices" ref="slices">
            </g>
            <text class="detailsText" ref="detailsText" dy="-15"></text>
            <circle class="detailsCircle" ref="detailsCircle" v-bind:r="Math.max(0, this.radius) * .7"></circle>
        </svg>
        <transition-group v-if="slices" v-bind:duration="duration" tag="div" class="legend">
            <div v-for="slice in reversedSlices"
                 @mouseover="detailsDisplay(slice)"
                 @mouseout="detailsHide(slice)"
                 v-bind:id="'label_'+slice.data.key"
                 v-bind:key="slice.data.key">
                <span class="square" v-bind:style="'background:'+slice.data.color"></span>
                <span>{{ slice.data.label }}</span>
            </div>
        </transition-group>
    </div>
</template>

<script>
    module.exports = {
        props: {
            debate: Object,
            count: {type: Function, default: d => d.count},
            duration: {type: Number, default: 750}
        },
        data() {
            return  {
                byLetter: false,
                byOpinion: false,
                pie: d3.pie()
                        .value(d => this.byLetter ? d.letterCount : d.commentCount)
                        .sort((a, b) => a.id >= b.id ? 1 : -1),
                radius: 0,
                commentCount: 0,
                letterCount: 0,
                /*d3.interpolateSinebow, d3.interpolateRainbow,
                 d3.interpolateCool, d3.interpolateWarm, d3.interpolateCividis,
                 d3.interpolatePlasma, d3.interpolateViridis, d3.interpolateSpectral,
                 d3.interpolateRdYlGn, d3.interpolateRdYlBu*/
                margin: {top: 10, right: 10, bottom: 10, left: 10}

            }
        },
        computed: {
            reversedSlices() {
                return this.slices.reverse();
            },
            arc() {
                return d3.arc().outerRadius(this.radius * .95)
                        .innerRadius(this.radius * .75)
                        .padAngle(.005);
            },
            outterArc() {
                return d3.arc().outerRadius(this.radius)
                        .innerRadius(this.radius * .8)
                        .cornerRadius(0)
                        .padAngle(.005);
            },
            data() {
                if (this.debate.comments) {
                    let data = d3.nest()
                            .key((d) => this.byOpinion ? d.opinion : d.owner.fullName())
                            .entries(this.debate.comments)
                            .sort((a, b) => a.label > b.label ? -1 : 1);
                    data.forEach((d, i) => {
                        d.label = this.byOpinion ? this.$t('comment.' + d.key.toLowerCase()) : d.values[0].owner.fullName();
                        d.color = this.byOpinion
                                ? (d.key === 'FOR' ? blueColor
                                        : d.key === 'AGAINST' ? redColor
                                        : d.key === 'NEUTRAL' ? greyColor : '')
                                : d3.interpolateRainbow(i / data.length);
                        d.letterCount = d.values.reduce((acc, c) => acc + c.argumentation.length, 0);
                        d.commentCount = d.values.length;
                    });
                    this.letterCount = data.reduce((acc, d) => acc + d.letterCount, 0);
                    this.commentCount = data.reduce((acc, d) => acc + d.commentCount, 0);
                    return data;
                }
                return [];
            },
            slices() {
                let slices = this.pie(this.data);
                slices.forEach((d, i) => d.id = i);
                return slices;
            }
        },
        watch: {
            debate() {
                this.update(0);
            },
            byLetter() {
                this.update(this.duration);
            },
            byOpinion() {
                this.update(this.duration);
            }
        },
        mounted() {
            this.update();
            window.addEventListener('resize', d => this.update());
        },
        methods: {
            updatePosition() {
                let height = Math.max(0, Math.min(this.$el.offsetWidth / 2, this.$el.offsetHeight - 40));
                this.radius = (height - this.margin.top - this.margin.bottom) / 2;
                let width = this.radius * 2 + this.margin.left + this.margin.right;

                // POSITIONS //
                d3.select(this.$refs.svg)
                        .attr("viewBox", [-this.radius - this.margin.left, -this.margin.top - this.radius, width, height])
                        .attr("width", width)
                        .attr("height", height);
            },
            update(duration = 0) {
                this.updatePosition();

                // SLICES //
                const slice = d3.select(this.$refs.slices).selectAll("path")
                        .data(this.slices, d => d.id);
                const sliceEnter = slice.enter().append("path")
                        .each((d, i, a) => {
                            a[i]._current = d;
                        })
                        .on('mouseenter', this.detailsDisplay)
                        .on('mouseout', this.detailsHide);

                // Transition slice to their new size.
                slice.merge(sliceEnter).transition().duration(duration)
                        .attr('fill', d => d.data.color)
                        .attr("fill-opacity", 1)
                        .attr("stroke-opacity", 1)
                        .attrTween("d", this.arcTween);
                // Transition exiting slices to the void.
                slice.exit().transition().duration(duration).remove()
                        .attr("fill-opacity", 0)
                        .attr("stroke-opacity", 0)
                        .attrTween("d", this.arcTween);
            },
            midAngle(d) {
                return d.startAngle + (d.endAngle - d.startAngle) / 2;
            },
            arcTween(d, i, a) {
                let to = {
                    startAngle: d.startAngle,
                    endAngle: d.endAngle,
                };
                let interpol = d3.interpolate(a[i]._current, to);
                a[i]._current = interpol(0);
                return (t) => this.arc(interpol(t));
            },
            detailsDisplay(d) {
                d3.select(this.$refs.svg).selectAll("path")
                        .filter((s) => s === d)
                        .transition().duration(this.duration / 2)
                        .attr("d", this.outterArc);
                d3.select(this.$refs.detailsCircle).style('fill', d.data.color);
                d3.select(this.$refs.detailsText).html(this.details(d));
                let legendItem = document.getElementById('label_' + d.data.key);
                legendItem.classList.add("active");
            },
            detailsHide(d) {
                d3.select(this.$refs.svg).selectAll("path")
                        .filter((s) => s === d)
                        .transition().duration(this.duration / 2)
                        .attr("d", this.arc);
                d3.select(this.$refs.detailsCircle).style('fill', 'none');
                d3.select(this.$refs.detailsText).html("");
                let legendItem = this.$el.querySelector('div.active[id^="label_"]');
                legendItem.classList.remove("active");
            },
            details(d) {
                return '<tspan x="0">' + d.data.label + '</tspan>'
                        + '<tspan x="0" dy="1.2em">' + this.$tc('nb_comment', d.data.commentCount)
                        + ' (' + d3.format(',.2%')(d.data.commentCount / this.commentCount) + ')</tspan>'
                        + '<tspan x="0" dy="1.2em">' + this.$tc('nb_letter', d.data.letterCount)
                        + ' (' + d3.format(',.2%')(d.data.letterCount / this.letterCount) + ')</tspan>';
            }
        }
    };
</script>