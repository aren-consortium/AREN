<template>
    <div class="D3_tree">
        <svg ref="svg" viewBox="0 0 1 1">
            <text class="material-icons" dx="-24" dy="10"
                  @mouseenter="displayDetail(root)"
                  @mouseleave="hideDetail(root)">description</text>
            <g class="links" ref="links">
            </g>
            <g class="nodes" ref="nodes">
            </g>
        </svg>
        <div v-if="detail" class="detail z-depth-2">
            <div v-if="detail === debate" class="document" v-html="detail.document.content"></div>
            <comment-widget v-else
                            v-bind:preview="true"
                            v-bind:comment="detail"
                            @close="detail=false">
            </comment-widget>
        </div>
</template>

<script>
    module.exports = {
        props: {
            debate: Object,
            duration: {type: Number, default: 750},
            dy: {type: Number, default: 180},
            dx: {type: Number, default: 15}
        },
        data() {
            return {
                root: false,
                detail: false,
                tree: d3.tree().nodeSize([this.dx, this.dy]),
                margin: {top: 20, right: 20, bottom: 20, left: 20}
            }
        },
        mounted() {
            this.init();
        },
        watch: {
            debate(val) {
                if (val) {
                    this.init();
                }
            }
        },
        methods: {
            init() {
                this.root = d3.hierarchy(this.debate, d => d === this.debate ? d.comments.filter(c => !c.parent) : d.comments);
                this.root.x0 = 0;
                this.root.y0 = 0;
                this.root.descendants().forEach((d, i) => {
                    d.id = i;
                    d._children = d.children;
                });
                this.update(this.root, 0);
            },
            diagonal: d3.linkHorizontal().x(d => d.y).y(d => d.x),
            update(source = this.root, duration = 0) {
                const nodes = this.root.descendants().slice(1).reverse();
                const links = this.root.links();

                // Compute the new tree layout.
                this.tree(this.root);

                const bottom = Math.max.apply(Math, nodes.map((o) => o.x));
                const top = Math.min.apply(Math, nodes.map((o) => o.x));
                const height = bottom - top + this.margin.top + this.margin.bottom;
                const width = this.root.height * this.dy + this.margin.left + this.margin.right;

                d3.select(this.$refs.svg).transition().duration(duration)
                        .attr("viewBox", [-this.margin.left, top - this.margin.top, width, height])
                        .attr("width", width)
                        .attr("height", height);

                // Update the nodes…
                const node = d3.select(this.$refs.nodes).selectAll("g")
                        .data(nodes, d => d.id);
                // Enter any new nodes at the parent's previous position.
                const nodeEnter = node.enter().append("g")
                        .attr("transform", d => `translate(${source.y0},${source.x0})`)
                        .attr("class", d => d._children ? "with-children " : '');

                nodeEnter.append("circle")
                        .attr("class", d => "bullet " + (d.data.opinion ? d.data.opinion.toLowerCase() : ''))
                        .attr("fill", d => d._children ? "white" : this.color(d))
                        .attr("stroke", d => d._children ? this.color(d) : "none")
                        .attr("stroke-width", d => d._children ? 2 : 0)
                        .attr("r", d => d._children ? 4 : 5)
                        .on('mouseenter', this.displayDetail)
                        .on('mouseleave', this.hideDetail)
                        .on("click", d => this.$emit("bullet", d.data));

                nodeEnter.append("text")
                        .attr("dy", ".31em")
                        .attr("x", -12)
                        .style("text-anchor", "end")
                        .text(d => d.data !== this.debate ? d.data.owner.fullName() : '')
                        .on("click", d => {
                            d.children = d.children ? null : d._children;
                            this.update(d, this.duration);
                        })
                        .clone(true).lower()
                        .attr("stroke-linejoin", "round")
                        .attr("stroke-width", 3)
                        .attr("stroke", "white");

                // Transition nodes to their new position.
                node.merge(nodeEnter)
                        .transition().duration(duration)
                        .attr("transform", d => `translate(${d.y},${d.x})`)
                        .attr("fill-opacity", 1)
                        .attr("stroke-opacity", 1);
                // Transition exiting nodes to the parent's new position.
                node.exit().transition().duration(duration).remove()
                        .attr("transform", d => `translate(${source.y},${source.x})`)
                        .attr("fill-opacity", 0)
                        .attr("stroke-opacity", 0);

                // Update the links…
                const link = d3.select(this.$refs.links).selectAll("path")
                        .data(links, d => d.target.id);
                // Enter any new links at the parent's previous position.
                const linkEnter = link.enter().append("path")
                        .attr("d", d => {
                            const o = {x: source.x0, y: source.y0};
                            return this.diagonal({source: o, target: o});
                        });

                // Transition links to their new position.
                link.merge(linkEnter).transition().duration(duration)
                        .attr("d", this.diagonal);
                // Transition exiting nodes to the parent's new position.
                link.exit().transition().duration(duration).remove()
                        .attr("d", d => {
                            const o = {x: source.x, y: source.y};
                            return this.diagonal({source: o, target: o});
                        });

                // Stash the old positions for transition.
                this.root.eachBefore(d => {
                    d.x0 = d.x;
                    d.y0 = d.y;
                });
            },
            color(d) {
                return d.data === this.debate ? "#F8ECE7"
                        : d.data.opinion === 'FOR' ? blueColor
                        : d.data.opinion === 'AGAINST' ? redColor
                        : d.data.opinion === 'NEUTRAL' ? greyColor : '';
            },
            displayDetail(d) {
                d.timeout = setTimeout(() => {
                    this.detail = d.data;
                }, 50);
            },
            hideDetail(d) {
                clearTimeout(d.timeout)
            }
        },
        components: {
            'comment-widget': vueLoader('components/widgets/comment')
        }
    };
</script>