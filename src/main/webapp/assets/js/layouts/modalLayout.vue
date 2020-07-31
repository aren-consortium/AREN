<template>
    <transition>
        <div v-if="open" class="modal-wrapper">
            <div class="modal modal-fixed-footer"
                 v-bind:style="{top : f(top), right: f(right), bottom: f(bottom), left: f(left)}"
                 ref="modal">
                <div v-if="movable"
                     ref="dragger"
                     class="dragger"
                     @mousedown="dragStart($event)">
                </div>
                <div class="modal-content">
                    <h1><slot name="title"></slot></h1>
                    <slot></slot>
                </div>
                <div class="modal-footer">
                    <slot name="footer"></slot>
                </div>
            </div>
            <div v-if="!movable" class="modal-overlay"></div>
        </div>
    </transition>
</template>

<script>
    module.exports = {
        props: ['movable'],
        data() {
            return {
                open: false,
                top: false,
                right: false,
                bottom: false,
                left: false,
                dragging: false
            }
        },
        mounted() {
            this.$emit('mounted');
        },
        methods: {
            dragStart(e) {
                e = e || window.event;
                e.preventDefault();
                document.addEventListener("mouseup", this.dragStop);
                document.addEventListener("mousemove", this.dragMove);
                this.left = this.$refs.modal.offsetLeft;
                this.top = this.$refs.modal.offsetTop;
                this.right = "auto";
                this.bottom = "auto";
            },
            dragMove(e) {
                e = e || window.event;
                e.preventDefault();
                this.left += e.movementX;
                this.top += e.movementY;
            },
            dragStop(e) {
                document.removeEventListener("mouseup", this.dragStop);
                document.removeEventListener("mousemove", this.dragMove);
            },
            f(value) {
                return isNaN(value) ? value : value + "px";
            }
        }
    };
</script>
