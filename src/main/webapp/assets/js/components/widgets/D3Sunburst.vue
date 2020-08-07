<template>
    <div class="D3_sunburst">
        <toggle-action-button
            v-bind:off-label="$t('display_labels')"
            v-bind:on-label="$t('display_arguments')"
            v-model="showDetail">
        </toggle-action-button>
        <toggle-action-button
            v-bind:off-label="$t('by_user')"
            v-bind:on-label="$t('by_opinion')"
            v-model="byOpinion">
        </toggle-action-button>

        <svg ref="svg" viewBox="0 0 1 1">
            <g class="slices" ref="slices">
            </g>
        </svg>
        <div v-if="detail">
            <div v-if="detail" class="detail z-depth-2">
                <div v-if="detail === debate" class="document" v-html="detail.document.content"></div>
                <comment-widget v-else
                                v-bind:preview="true"
                                v-bind:comment="detail">
                </comment-widget>
            </div>
            <div v-else>{{ $t("helper.restitution_display_details")}}</div>
        </div>
        <transition-group v-else-if="root" v-bind:duration="duration" tag="div" class="legend">
            <div v-for="label in legendLabels"
                 @mouseover="highlight(d => byOpinion ? d.data.opinion && d.data.opinion === label.key : d.data.owner.id === label.key)"
                 @mouseout="unlight()"
                 v-bind:id="'label_'+label.key"
                 v-bind:key="label.key">
                <span class="square" v-bind:style="'background:'+label.color"></span>
                <span>{{ label.label }}</pan>
            </div>
        </transition-group>
    </div>
</template>

<script>
    module.exports = {
        props: {
            debate: Object,
            duration: {type: Number, default: 750}
        },
        data() {
            return {
                root: false,
                radius: 0,
                byOpinion: false,
                showDetail: false,
                focusOn: false,
                detail: false,
                opinionDisplay: [{key: "FOR", label: this.$t('comment.for'), color: blueColor},
                    {key: "AGAINST", label: this.$t('comment.against'), color: redColor},
                    {key: "NEUTRAL", label: this.$t('comment.neutral'), color: greyColor}],
                arc: d3.arc()
                        .startAngle(d => this.arcAngle(d.x0))
                        .endAngle(d => this.arcAngle(d.x1))
                        .innerRadius(d => Math.max(0, this.arcRadius(d.y0)))
                        .outerRadius(d => Math.max(0, this.arcRadius(d.y1)) - 1)
                        .padAngle(d => this.padAngle(d.depth - this.focusOn.depth)),
                margin: {top: 10, right: 10, bottom: 10, left: 10}
            }
        },
        computed: {
            padAngle() {
                return d3.scaleLinear().domain([0, this.focusOn.height + 1]).range([0.005, 0]);
            },
            arcAngle() {
                return d3.scaleLinear()
                        .range([0, 2 * Math.PI])
                        .clamp(true);
            },
            arcRadius() {
                return d3.scalePow().exponent(1 / 1.8).range([this.radius / 10, this.radius]);
            },
            userDisplay() {
                return this.debate.comments.map(c => c.owner)
                        .filter((u, i, a) => a.indexOf(u) === i) //unique
                        .sort((a, b) => a.fullName() < b.fullName() ? -1 : 1)
                        .map((u, i, a) => ({
                                key: u.id,
                                label: u.fullName(),
                                color: d3.interpolateRainbow(i / a.length)
                            }));
            },
            focusedUser() {
                let keys = this.focusOn.descendants().map(c => c.data.owner.id);
                if (this.focusOn.parent && this.focusOn.data !== this.debate) {
                    keys.push(this.focusOn.parent.data.owner.id);
                }
                return this.userDisplay.filter(u => keys.includes(u.key));
            },
            focusedOpinion() {
                let keys = this.focusOn.descendants().map(c => c.data.opinion);
                if (this.focusOn.parent && this.focusOn.data !== this.debate) {
                    keys.push(this.focusOn.parent.data.opinion);
                }
                return this.opinionDisplay.filter(u => keys.includes(u.key));
            },
            legendLabels() {
                return this.byOpinion ? this.focusedOpinion : this.focusedUser;
            },
            labelDisplay() {
                return this.byOpinion ? this.opinionDisplay : this.userDisplay;
            }
        },
        watch: {
            debate(val) {
                if (val) {
                    this.update();
                }
            },
            focusOn() {
                this.update();
            },
            byOpinion() {
                this.update();
            },
            showDetail() {
                this.detail = false;
                this.unlight();
            }
        },
        mounted() {
            this.init();
            window.addEventListener('resize', d => this.update());
        },
        methods: {
            init() {
                this.root = d3.hierarchy(this.debate, d => d === this.debate ? d.comments.filter(c => !c.parent) : d.comments)
                        .sum(d => d.comments.length > 0 ? 0 : 1);
                d3.partition()(this.root);
                this.focusOn = this.root;
                this.update(0);
            },
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
            update(duration = this.duration) {
                this.updatePosition();

                this.root.each(d => d.visible = false);
                this.focusOn.each(d => d.visible = true);
                if (this.focusOn.parent) {
                    this.focusOn.parent.visible = true;
                }

                // SLICES //
                const slice = d3.select(this.$refs.slices).selectAll("path")
                        .data(this.root.descendants());

                const sliceEnter = slice.enter().append("path")
                        .attr("class", d => d.data === this.debate ? 'debate' : '')
                        .on('click', d => this.focusOn = d)
                        .on('mouseenter', this.displayDetail)
                        .on('mouseleave', this.hideDetail);

                // Transition slice to their new size.
                slice.merge(sliceEnter).transition().duration(duration)
                        .attr('fill', this.color)
                        .attr("fill-opacity", d => d.visible ? 1 : 0)
                        .attr("stroke-opacity", d => d.visible ? 1 : 0)
                        .tween('scale', () => this.scaleTween(this.focusOn))
                        .attrTween('d', d => () => this.arc(d));

                // Transition exiting slices to the void.
                slice.exit().transition().duration(duration).remove()
                        .attr("fill-opacity", 0)
                        .attr("stroke-opacity", 0)
                        .tween('scale', () => this.scaleTween(this.focusOn))
                        .attrTween('d', d => () => this.arc(d));
            },
            color(d) {
                if (d.data !== this.debate) {
                    return this.labelDisplay.find(l => l.key === (this.byOpinion ? d.data.opinion : d.data.owner.id)).color;
                }
                return "#F8ECE7";
            },
            scaleTween(d) {
                // Interpolation of the arcAngle and arcRadius function domain
                const xd = d3.interpolate(this.arcAngle.domain(), [d.x0, d.x1]),
                        yd = d3.interpolate(this.arcRadius.domain(), [d.y0, 1]);
                return t => {
                    this.arcAngle.domain(xd(t));
                    this.arcRadius.domain(yd(t));
                };
            },
            displayDetail(d) {
                if (this.showDetail) {
                    d.timeout = setTimeout(() => {
                        this.highlight(c => c === d);
                        this.detail = d.data;
                    }, 50);
                } else if (d.data !== this.debate) {
                    let commentsIds = [d.data.id];
                    d.data.comments.forEach(function addChild(child) {
                        commentsIds.push(child.id);
                        child.comments.forEach(addChild);
                    });
                    this.highlight(c => commentsIds.includes(c.data.id));
                    let legendItem = document.getElementById('label_' + (this.byOpinion ? d.data.opinion : d.data.owner.id));
                    legendItem.classList.add("active");
                }
            },
            hideDetail(d) {
                if (this.showDetail) {
                    clearTimeout(d.timeout)
                } else if (d.data !== this.debate) {
                    this.unlight();
                    let legendItem = this.$el.querySelector('div.active[id^="label_"]');
                    legendItem.classList.remove("active");
                }
            },
            highlight(filter) {
                d3.select(this.$refs.slices).selectAll("path")
                        .style("opacity", .35)
                        .filter(filter)
                        .style("opacity", 1);
            },
            unlight() {
                d3.select(this.$refs.slices).selectAll("path")
                        .style("opacity", 1);
            }
        },
        components: {
            'comment-widget': vueLoader('components/widgets/comment')
        }
    };
</script>