<template>
    <transition>
        <div v-if="show" id="documentation" v-bind:style="style" v-bind:class="{first: first}">
            <div class="overlay" v-bind:style="overlayStyle"></div>
            <svg v-if="target">
                <path v-bind:d="arrowPath"></path>
            </svg>
            <div class="content" v-html="value"></div>
            <div class="next fixed-action-btn btn-floating btn-large" @click="next()"><i class="material-icons">arrow_forward</i></div>
            <div class="close fixed-action-btn btn-floating btn-small red" @click="hide()"><i class="material-icons">close</i></div>
        </div>
    </transition>
</template>

<script>
    module.exports = {
        data() {
            return {
                show: false,
                value: "",
                overlayStyle: {top: "", left: "", bottom: "", right: ""},
                style: {top: "", left: "", width: "", height: ""},
                arrowPath: "",
                first: true
            }
        },
        mounted() {
            if (!Vue.prototype.$documentation) {
                Vue.prototype.$documentation = {};
                Vue.prototype.$documentation.display = this.display;
                Vue.prototype.$documentation.hide = this.hide;
                Vue.prototype.$documentation.next = this.next;
            } else {
                throw "This is a singleton component you cannot have many of if in the same app";
            }
        },
        methods: {
            display(value, el) {
                if (this.$root.help && value && value.length > 0) {
                    this.target = el;
                    this.show = true;
                    this.value = value;

                    this.style.top = 0;
                    this.style.left = 0;
                    this.style.bottom = 0;
                    this.style.right = 0;

                    if (el) {
                        let box = this.getVisibleRect(el);

                        this.overlayStyle.top = (box.top + box.height / 2) + "px";
                        this.overlayStyle.left = (box.left + box.width / 2) + "px";
                        this.overlayStyle.width = (box.width + 10) + "px";
                        this.overlayStyle.height = (box.height + 10) + "px";

                        let topArea = box.top * window.innerWidth;
                        let bottomArea = (window.innerHeight - box.bottom) * window.innerWidth;
                        let leftArea = box.left * window.innerHeight;
                        let rightArea = (window.innerWidth - box.right) * window.innerHeight;
                        let maxArea = Math.max(topArea, bottomArea, leftArea, rightArea);

                        switch (maxArea) {
                            case topArea:
                                this.style.bottom = (window.innerHeight - box.top) + "px";
                                break;
                            case bottomArea:
                                this.style.top = box.bottom + "px";
                                break;
                            case leftArea:
                                this.style.right = (window.innerWidth - box.left) + "px";
                                break;
                            case rightArea:
                                this.style.left = box.right + "px";
                                break;
                        }

                        this.$nextTick(() => {
                            let vertical = false;
                            let contentBox = this.$el.querySelector(".content").getBoundingClientRect();
                            let source = {};
                            let target = {};
                            if (box.right < (contentBox.left - 20)) {
                                source.x = contentBox.left - 10;
                                source.y = contentBox.top + contentBox.height / 2;
                                target.x = box.right + 15;
                                target.y = box.top + box.height / 2;
                            } else if (box.left > (contentBox.right + 20)) {
                                source.x = contentBox.right + 10;
                                source.y = contentBox.top + contentBox.height / 2;
                                target.x = box.left - 15;
                                target.y = box.top + box.height / 2;
                            } else {
                                vertical = true;
                                source.x = contentBox.left + (contentBox.width / 2);
                                target.x = box.left + (box.width / 2);
                                if (box.bottom < (contentBox.top - 20)) {
                                    source.y = contentBox.top - 10;
                                    target.y = box.bottom + 15;
                                } else if (box.top > (contentBox.bottom + 20)) {
                                    source.y = contentBox.bottom + 15;
                                    target.y = box.top - 10;
                                }
                            }
                            if (vertical) {
                                this.arrowPath = d3.linkVertical().x(d => d.x).y(d => d.y)({source, target});
                            } else {
                                this.arrowPath = d3.linkHorizontal().x(d => d.x).y(d => d.y)({source, target});
                            }
                            this.first = false;
                        });
                    } else {
                        this.overlayStyle.top = 0;
                        this.overlayStyle.left = 0;
                        this.overlayStyle.width = 0;
                        this.overlayStyle.height = 0;
                    }
                }
            },
            hide() {
                this.show = false;
                this.first = true;
                this.$root.help = false;
            },
            getVisibleRect(el) {
                let box = el.getBoundingClientRect();
                el = el.parentElement;
                while (el !== document.body) {
                    if (el.offsetHeight < el.scrollHeight) {
                        box.y += el.scrollTop;
                        if ((el.offsetHeight - el.offsetTop) < box.height) {
                            box.height = el.offsetHeight - el.offsetTop;
                        }
                    }
                    if (el.offsetWidth < el.scrollWidth) {
                        box.x += el.scrollLeft;
                        if (el.offsetWidth < box.width) {
                            box.width = el.offsetWidth;
                        }
                    }
                    el = el.parentElement;
                }
                return box;
            },
            next() {
                let targets = [...document.querySelectorAll('.documented')];
                if (targets.length > 0) {
                    let index = targets.findIndex((e) => e === this.target);
                    targets[ (index + 1) % targets.length ].dispatchEvent(new Event('mouseenter'));
                }
            }
        }
    };
</script>