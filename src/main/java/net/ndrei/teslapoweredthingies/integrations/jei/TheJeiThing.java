package net.ndrei.teslapoweredthingies.integrations.jei;

import mezz.jei.api.*;

/**
 * Created by CF on 2017-04-13.
 */
@JEIPlugin
public class TheJeiThing extends BlankModPlugin {
    @Override
    public void register(IModRegistry registry) {
        super.register(registry);
        IJeiHelpers jeiHelpers = registry.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

        FluidBurnerCategory.register(registry, guiHelper);
        FluidSolidifierCategory.register(registry, guiHelper);
        IncineratorCategory.register(registry, guiHelper);
    }
}
