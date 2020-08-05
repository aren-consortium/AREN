<script>
    module.exports = {
        props: ['value'],
        render() {
            if (this.$slots.default && this.$slots.default.length <= 1) {
                if (!this.$slots.default[0].data) {
                    this.$slots.default[0].data = {}
                }
                let data = this.$slots.default[0].data;
                if (!data.staticClass) {
                    data.staticClass = "";
                }
                if (!data.on) {
                    data.on = {};
                }
                data.staticClass += ' tooltiped';
                data.on.mouseover = () => this.$tooltip.display(this.value, this.$el);
                data.on.mouseleave = () => this.$tooltip.hide();
                data.on.mousedown = () => this.$tooltip.hide();
                data.on.wheel = () => this.$tooltip.hide();
                return this.$slots.default[0];
            } else {
                throw new Error('documentation.vue can only render one, and exactly one child component.');
            }
            return null;
        }
    };
</script>