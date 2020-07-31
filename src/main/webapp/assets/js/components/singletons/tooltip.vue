<template>
    <transition>
        <div v-if="show" id="tooltip" v-html="value" v-bind:style="style"></div>
    </transition>
</template>

<script>
    module.exports = {
        data() {
            return {
                show: false,
                value: "",
                style: {top: "", left: ""}
            }
        },
        mounted() {
            if (!Vue.prototype.$tooltip) {
                Vue.prototype.$tooltip = {};
                Vue.prototype.$tooltip.display = this.display;
                Vue.prototype.$tooltip.hide = this.hide;
            } else {
                throw "This is a singleton component you cannot have many of if in the same app";
            }
        },
        methods: {
            display(value, el) {
                if (value && value.length > 0) {
                    this.show = true;
                    this.value = value;
                    let box = el.getBoundingClientRect();
                    this.style.top = box.top + "px";
                    this.style.left = (box.left + box.width / 2) + "px";
                }
            },
            hide() {
                this.show = false;
            }
        }
    };
</script>