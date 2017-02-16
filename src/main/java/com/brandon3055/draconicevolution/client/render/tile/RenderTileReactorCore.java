package com.brandon3055.draconicevolution.client.render.tile;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCOBJParser;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.state.GlStateManagerHelper;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.brandon3055.brandonscore.client.render.TESRBase;
import com.brandon3055.draconicevolution.blocks.reactor.tileentity.TileReactorCore;
import com.brandon3055.draconicevolution.client.handler.ClientEventHandler;
import com.brandon3055.draconicevolution.client.render.shaders.DEShaders;
import com.brandon3055.draconicevolution.helpers.ResourceHelperDE;
import com.brandon3055.draconicevolution.utils.DETextures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.Map;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class RenderTileReactorCore extends TESRBase<TileReactorCore> {
    private CCModel model;
    private CCModel model_no_shade;

    public RenderTileReactorCore() {
        Map<String, CCModel> map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/block/obj_models/reactor_core.obj"));
        model = CCModel.combine(map.values());
        map = CCOBJParser.parseObjModels(ResourceHelperDE.getResource("models/reactor_core_model.obj"));
        model_no_shade = CCModel.combine(map.values());
    }

    @Override
    public void renderTileEntityAt(TileReactorCore te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();
        GlStateManagerHelper.pushState();
        GlStateManager.disableLighting();
        setLighting(200);
        float scale = 2;
        float intensity = 1;
        float animation = (ClientEventHandler.elapsedTicks + partialTicks) / 20F;

        if (DEShaders.useShaders()) {
            DEShaders.reactorOp.setAnimation(animation);
        }

        if (MinecraftForgeClient.getRenderPass() == 0) {
            renderCore(x, y, z, partialTicks, intensity, animation, scale, DEShaders.useShaders());
        }
        else {
            renderShield(x, y, z, partialTicks, 0.7F, scale, DEShaders.useShaders());
        }

        resetLighting();
        GlStateManagerHelper.popState();
        GlStateManager.popMatrix();
    }

    public void renderItem() {
        GlStateManager.pushMatrix();
        GlStateManagerHelper.pushState();
        GlStateManager.disableLighting();
        setLighting(200);
        float scale = 1.5F;
        float intensity = 0;

        renderCore(0, 0, 0, 0, intensity, 0, scale, DEShaders.useShaders());

        resetLighting();
        GlStateManagerHelper.popState();
        GlStateManager.popMatrix();
    }

    private void renderCore(double x, double y, double z, float partialTicks, float intensity, float animation, float scale, boolean useShader) {
        ResourceHelperDE.bindTexture(DETextures.REACTOR_CORE);
        if (useShader) {
            DEShaders.reactorOp.setIntensity(intensity);
            DEShaders.reactor.freeBindShader();
        }


        CCRenderState ccrs = CCRenderState.instance();
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0), scale);
        model.render(ccrs, mat);
        ccrs.draw();

        if (useShader) {
            ShaderProgram.unbindShader();
        }
    }

    private void renderShield(double x, double y, double z, float partialTicks, float intensity, float scale, boolean useShader) {
        ResourceHelperDE.bindTexture(DETextures.REACTOR_SHIELD);
        if (useShader) {
            DEShaders.reactorOp.setIntensity(intensity);
            DEShaders.reactorShield.freeBindShader();
        }
        else {
            float ff = 0.5F;//tile.maxFieldCharge > 0 ? tile.fieldCharge / tile.maxFieldCharge : 0;
            float r = ff < 0.5F ? 1 - (ff * 2) : 0;
            float g = ff > 0.5F ? (ff - 0.5F) * 2 : 0;
            float b = ff * 2;
            float a = ff < 0.1F ? (ff * 10) : 1;
            GlStateManager.color(r, g, b, a);
        }

        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);

        if (DEShaders.useShaders()) {
            Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0), scale * 1.05);
            model.render(ccrs, mat);
        }
        else {
            Matrix4 mat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.5, z + 0.5), new Rotation((ClientEventHandler.elapsedTicks + partialTicks) / 400F, 0, 1, 0), scale * -0.525);
            model_no_shade.render(ccrs, mat);
        }

        ccrs.draw();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        if (useShader) {
            ShaderProgram.unbindShader();
        }
    }

}
