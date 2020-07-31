<template>
    <div>
        <div id="editor"></div>
    </div>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                editor: false
            };
        },
        props: ['value', 'enabled', 'toolbar', 'theme'],
        watch: {
            value( ) {
                if (!this.selfChange) {
                    this.editor.root.innerHTML = this.value;
                }
                this.selfChange = false;
            },
            enabled( ) {
                this.enabled ? this.editor.enable( ) : this.editor.disable( );
            }
        },
        mounted( ) {
            this.editor = new Quill(this.$el, {
                modules: {
                    toolbar: this.toolbar || [
                        [{header: [1, 2, 3, 4, false]}],
                        ['bold', 'italic', 'underline'],
                        [{'align': ''}, {'align': 'center'}, {'align': 'right'}],
                        [{'color': []}, {'background': []}],
                        ['link', 'image', 'video'],
                        ['clean']
                    ]
                },
                theme: this.theme || 'snow'
            });
            this.editor.root.innerHTML = this.value || "";
            this.enabled ? this.editor.enable( ) : this.editor.disable( );
            this.editor.on('text-change', (delta, oldDelta, source) => {
                this.selfChange = true;
                this.$emit('input', this.editor.root.innerHTML);
            });
        }
    };
</script>